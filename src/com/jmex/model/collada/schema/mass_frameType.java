/**
 * mass_frameType.java
 *
 * This file was generated by XMLSpy 2007sp2 Enterprise Edition.
 *
 * YOU SHOULD NOT MODIFY THIS FILE, BECAUSE IT WILL BE
 * OVERWRITTEN WHEN YOU RE-RUN CODE GENERATION.
 *
 * Refer to the XMLSpy Documentation for further details.
 * http://www.altova.com/xmlspy
 */


package com.jmex.model.collada.schema;


public class mass_frameType extends com.jmex.xml.xml.Node {

	public mass_frameType(mass_frameType node) {
		super(node);
	}

	public mass_frameType(org.w3c.dom.Node node) {
		super(node);
	}

	public mass_frameType(org.w3c.dom.Document doc) {
		super(doc);
	}

	public mass_frameType(com.jmex.xml.xml.Document doc, String namespaceURI, String prefix, String name) {
		super(doc, namespaceURI, prefix, name);
	}
	
	public void adjustPrefix() {
		for (	org.w3c.dom.Node tmpNode = getDomFirstChild( Element, "http://www.collada.org/2005/11/COLLADASchema", "translate" );
				tmpNode != null;
				tmpNode = getDomNextChild( Element, "http://www.collada.org/2005/11/COLLADASchema", "translate", tmpNode )
			) {
			internalAdjustPrefix(tmpNode, true);
			new TargetableFloat3(tmpNode).adjustPrefix();
		}
		for (	org.w3c.dom.Node tmpNode = getDomFirstChild( Element, "http://www.collada.org/2005/11/COLLADASchema", "rotate" );
				tmpNode != null;
				tmpNode = getDomNextChild( Element, "http://www.collada.org/2005/11/COLLADASchema", "rotate", tmpNode )
			) {
			internalAdjustPrefix(tmpNode, true);
			new rotateType(tmpNode).adjustPrefix();
		}
	}
	public void setXsiType() {
 		org.w3c.dom.Element el = (org.w3c.dom.Element) domNode;
		el.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "xsi:type", "mass_frame");
	}

	public static int gettranslateMinCount() {
		return 1;
	}

	public static int gettranslateMaxCount() {
		return 1;
	}

	public int gettranslateCount() {
		return getDomChildCount(Element, "http://www.collada.org/2005/11/COLLADASchema", "translate");
	}

	public boolean hastranslate() {
		return hasDomChild(Element, "http://www.collada.org/2005/11/COLLADASchema", "translate");
	}

	public TargetableFloat3 newtranslate() {
		return new TargetableFloat3(domNode.getOwnerDocument().createElementNS("http://www.collada.org/2005/11/COLLADASchema", "translate"));
	}

	public TargetableFloat3 gettranslateAt(int index) throws Exception {
		return new TargetableFloat3(getDomChildAt(Element, "http://www.collada.org/2005/11/COLLADASchema", "translate", index));
	}

	public org.w3c.dom.Node getStartingtranslateCursor() throws Exception {
		return getDomFirstChild(Element, "http://www.collada.org/2005/11/COLLADASchema", "translate" );
	}

	public org.w3c.dom.Node getAdvancedtranslateCursor( org.w3c.dom.Node curNode ) throws Exception {
		return getDomNextChild( Element, "http://www.collada.org/2005/11/COLLADASchema", "translate", curNode );
	}

	public TargetableFloat3 gettranslateValueAtCursor( org.w3c.dom.Node curNode ) throws Exception {
		if( curNode == null )
			throw new com.jmex.xml.xml.XmlException("Out of range");
		else
			return new TargetableFloat3(curNode);
	}

	public TargetableFloat3 gettranslate() throws Exception 
 {
		return gettranslateAt(0);
	}

	public void removetranslateAt(int index) {
		removeDomChildAt(Element, "http://www.collada.org/2005/11/COLLADASchema", "translate", index);
	}

	public void removetranslate() {
		removetranslateAt(0);
	}

	public org.w3c.dom.Node addtranslate(TargetableFloat3 value) {
		return appendDomElement("http://www.collada.org/2005/11/COLLADASchema", "translate", value);
	}

	public void inserttranslateAt(TargetableFloat3 value, int index) {
		insertDomElementAt("http://www.collada.org/2005/11/COLLADASchema", "translate", index, value);
	}

	public void replacetranslateAt(TargetableFloat3 value, int index) {
		replaceDomElementAt("http://www.collada.org/2005/11/COLLADASchema", "translate", index, value);
	}

	public static int getrotateMinCount() {
		return 1;
	}

	public static int getrotateMaxCount() {
		return 1;
	}

	public int getrotateCount() {
		return getDomChildCount(Element, "http://www.collada.org/2005/11/COLLADASchema", "rotate");
	}

	public boolean hasrotate() {
		return hasDomChild(Element, "http://www.collada.org/2005/11/COLLADASchema", "rotate");
	}

	public rotateType newrotate() {
		return new rotateType(domNode.getOwnerDocument().createElementNS("http://www.collada.org/2005/11/COLLADASchema", "rotate"));
	}

	public rotateType getrotateAt(int index) throws Exception {
		return new rotateType(getDomChildAt(Element, "http://www.collada.org/2005/11/COLLADASchema", "rotate", index));
	}

	public org.w3c.dom.Node getStartingrotateCursor() throws Exception {
		return getDomFirstChild(Element, "http://www.collada.org/2005/11/COLLADASchema", "rotate" );
	}

	public org.w3c.dom.Node getAdvancedrotateCursor( org.w3c.dom.Node curNode ) throws Exception {
		return getDomNextChild( Element, "http://www.collada.org/2005/11/COLLADASchema", "rotate", curNode );
	}

	public rotateType getrotateValueAtCursor( org.w3c.dom.Node curNode ) throws Exception {
		if( curNode == null )
			throw new com.jmex.xml.xml.XmlException("Out of range");
		else
			return new rotateType(curNode);
	}

	public rotateType getrotate() throws Exception 
 {
		return getrotateAt(0);
	}

	public void removerotateAt(int index) {
		removeDomChildAt(Element, "http://www.collada.org/2005/11/COLLADASchema", "rotate", index);
	}

	public void removerotate() {
		removerotateAt(0);
	}

	public org.w3c.dom.Node addrotate(rotateType value) {
		return appendDomElement("http://www.collada.org/2005/11/COLLADASchema", "rotate", value);
	}

	public void insertrotateAt(rotateType value, int index) {
		insertDomElementAt("http://www.collada.org/2005/11/COLLADASchema", "rotate", index, value);
	}

	public void replacerotateAt(rotateType value, int index) {
		replaceDomElementAt("http://www.collada.org/2005/11/COLLADASchema", "rotate", index, value);
	}

}
