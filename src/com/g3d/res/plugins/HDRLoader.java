package com.g3d.res.plugins;

import com.g3d.res.*;
import com.g3d.util.*;
import com.g3d.math.FastMath;
import com.g3d.texture.Image;
import com.g3d.texture.Image.Format;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.logging.Logger;

public class HDRLoader implements ContentLoader {

    private static final Logger logger = Logger.getLogger(HDRLoader.class.getName());
    
    private ByteBuffer rleTempBuffer;
    private ByteBuffer dataStore;
    private final float[] tempF = new float[3];
    
    private static Format imageFormat;
    private static float maxBright = 10f;

    private void convertRGBEtoFloat(byte[] rgbe){
        int R = rgbe[0] & 0xFF, 
            G = rgbe[1] & 0xFF,
            B = rgbe[2] & 0xFF, 
            E = rgbe[3] & 0xFF;
        
        float e = (float) Math.pow(2f, E - (128 + 8) );
        tempF[0] = R * e;
        tempF[1] = G * e;
        tempF[2] = B * e;
    }
    
//    private static void convertRGBEtoFloat2(byte[] rgbe, float[] store){
//        if (rgbe.length != 4 || store.length != 3)
//            throw new IllegalArgumentException();
//
//        int R = rgbe[0] & 0xFF,
//            G = rgbe[1] & 0xFF,
//            B = rgbe[2] & 0xFF,
//            E = rgbe[3] & 0xFF;
//
//        float e = (float) Math.pow(2f, E - 128);
//        store[0] = (R / 256.0f) * e;
//        store[1] = (G / 256.0f) * e;
//        store[2] = (B / 256.0f) * e;
//    }
    
    private short flip(int in){
        return (short) ((in << 8 & 0xFF00) | (in >> 8));
    }
    
    private void writeRGBE(byte[] rgbe){
        switch (imageFormat){
            case RGBA8:
                dataStore.put(rgbe[0]).put(rgbe[1]).put(rgbe[2]).put(rgbe[3]);
                break;
            case RGB16:
                convertRGBEtoFloat(rgbe);
                short v = flip((int) (tempF[0] * maxBright));
                dataStore.putShort(v);
                v = flip((int) (tempF[1] * maxBright));
                dataStore.putShort(v);
                v = flip((int) (tempF[2] * maxBright));
                dataStore.putShort(v);
                break;
            case RGBA16:
                dataStore.putShort(flip(rgbe[0] * 255)).putShort(flip(rgbe[1] * 255)).putShort(flip(rgbe[2] * 255)).putShort(flip(rgbe[3] * 255));
                break;
            case RGB16F:
            case RGB16F_to_RGB111110F:
            case RGB16F_to_RGB9E5:
                convertRGBEtoFloat(rgbe);
                dataStore.putShort(FastMath.convertFloatToHalf(tempF[0])).putShort(FastMath.convertFloatToHalf(tempF[1])).putShort(FastMath.convertFloatToHalf(tempF[2]));
                break;
            case RGB32F:
                convertRGBEtoFloat(rgbe);
                dataStore.putFloat(tempF[0]).putFloat(tempF[1]).putFloat(tempF[2]);
                break;

        }
    }
    
    private String readString(InputStream is) throws IOException{
        StringBuffer sb = new StringBuffer();
        while (true){
            int i = is.read();
            if (i == 0x0a || i == -1) // new line or EOF
                return sb.toString();
            
            sb.append((char)i);
        }
    }
    
    private boolean decodeScanlineRLE(InputStream in, int width) throws IOException{
        // must deocde RLE data into temp buffer before converting to float
        if (rleTempBuffer == null){
            rleTempBuffer = BufferUtils.createByteBuffer(width * 4);
        }else{
            rleTempBuffer.clear();
            if (rleTempBuffer.remaining() < width * 4)
                rleTempBuffer = BufferUtils.createByteBuffer(width * 4);
        }
        
	// read each component seperately
        for (int i = 0; i < 4; i++) {
            // read WIDTH bytes for the channel
            for (int j = 0; j < width;) {
                int code = in.read();
                if (code > 128) { // run
                    code -= 128;
                    int val = in.read();
                    while ((code--) != 0) {
                        rleTempBuffer.put( (j++) * 4 + i , (byte)val);
                        //scanline[j++][i] = val;
                    }
                } else {	// non-run
                    while ((code--) != 0) {
                        int val = in.read();
                        rleTempBuffer.put( (j++) * 4 + i, (byte)val);
                        //scanline[j++][i] = in.read();
                    }
                }
            }
        }
        
        rleTempBuffer.rewind();
        byte[] rgbe = new byte[4];
//        float[] temp = new float[3];
            
        // decode temp buffer into float data
        for (int i = 0; i < width; i++){
            rleTempBuffer.get(rgbe);
            writeRGBE(rgbe);
        }
        
        return true;
    }
    
    private boolean decodeScanlineUncompressed(InputStream in, int width) throws IOException{
        byte[] rgbe = new byte[4];
        float[] temp = new float[3];
        
        for (int i = 0; i < width; i+=3){
            if (in.read(rgbe) < 1)
                return false;

            writeRGBE(rgbe);
        }
        return true;
    }
    
    private void decodeScanline(InputStream in, int width) throws IOException{
        if (width < 8 || width > 0x7fff){
            // too short/long for RLE compression
            decodeScanlineUncompressed(in, width);
        }
        
        // check format
        byte[] data = new byte[4];
        in.read(data);
        if (data[0] != 0x02 || data[1] != 0x02 || (data[2] & 0x80) != 0){
            // not RLE data
            decodeScanlineUncompressed(in, width-1);
        }else{
            // check scanline width
            int readWidth = (data[2] & 0xFF) << 8 | (data[3] & 0xFF);
            if (readWidth != width)
                throw new IOException("Illegal scanline width in HDR file: "+width+" != "+readWidth);
            
            // RLE data
            decodeScanlineRLE(in, width);
        }
    }
    
    public Object load(ContentManager owner, InputStream is, String extension, ContentKey key) throws IOException {
        float gamma = -1f;
        float exposure = -1f;
        float[] colorcorr = new float[]{ -1f, -1f, -1f };
        
        int width = -1, height = -1;
        
        boolean verifiedFormat = false;
        
        while (true){
            String ln = readString(is);
            ln = ln.trim();
            if (ln.startsWith("#") || ln.equals("")){
                if (ln.equals("#?RADIANCE") || ln.equals("#?RGBE"))
                    verifiedFormat = true;
                
                continue; // comment or empty statement
            } else if (ln.startsWith("+") || ln.startsWith("-")){
                // + or - mark image resolution and start of data
                String[] resData = ln.split(" ");
                if (resData.length != 4){
                    throw new IOException("Invalid resolution string in HDR file");
                }
                
                if (!resData[0].equals("-Y") || !resData[2].equals("+X")){
                    logger.warning("Flipping/Rotating attributes ignored!");
                }
                
                //if (resData[0].endsWith("X")){
                    // first width then height
                //    width = Integer.parseInt(resData[1]);
                //    height = Integer.parseInt(resData[3]);
                //}else{
                    width = Integer.parseInt(resData[3]);
                    height = Integer.parseInt(resData[1]);
                //}
                
                break;
            } else {
                // regular command
                int index = ln.indexOf("=");
                if (index < 1){
                    logger.fine("Ignored string: "+ln);
                    continue;
                }
                
                String var = ln.substring(0, index).trim().toLowerCase();
                String value = ln.substring(index+1).trim().toLowerCase();
                if (var.equals("format")){
                    if (!value.equals("32-bit_rle_rgbe") && !value.equals("32-bit_rle_xyze")){
                        throw new IOException("Unsupported format in HDR picture");
                    }
                }else if (var.equals("exposure")){
                    exposure = Float.parseFloat(value);
                }else if (var.equals("gamma")){
                    gamma = Float.parseFloat(value);
                }else{
                    logger.warning("HDR Command ignored: "+ln);
                }
            }
        }
        
        assert width != -1 && height != -1;
        
        if (!verifiedFormat)
            logger.warning("Unsure if specified image is Radiance HDR");

        String formatStr = owner.getProperty("HDRFormat");
        if (formatStr != null){
            imageFormat = Format.valueOf(formatStr);
        }else{
            imageFormat = Format.RGB16F;
        }

        // some HDR images can get pretty big
        System.gc();

        // each pixel times size of component times # of components
        dataStore = BufferUtils.createByteBuffer(width * height * imageFormat.getBitsPerPixel());

        String flipImage = owner.getProperty("FlipImages");
        boolean flip = flipImage != null && flipImage.equals("true");
        int bytesPerPixel = imageFormat.getBitsPerPixel() / 8;
        int scanLineBytes = bytesPerPixel * width;
        for (int y = height - 1; y >= 0; y--) {
            if (flip)
                dataStore.position(scanLineBytes * y);
            
            decodeScanline(is, width);
        }
        
        dataStore.rewind();
        return new Image(imageFormat, width, height, dataStore);
    }

}
