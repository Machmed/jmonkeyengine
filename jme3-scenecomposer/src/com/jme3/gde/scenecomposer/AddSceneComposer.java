/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jme3.gde.scenecomposer;

import com.jme3.gde.core.assets.ProjectAssetManager;
import com.jme3.scene.Spatial;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;

public final class AddSceneComposer implements ActionListener {

    private final DataObject context;

    public AddSceneComposer(DataObject context) {
        this.context = context;
    }

    public void actionPerformed(ActionEvent ev) {
        ProjectAssetManager manager=context.getLookup().lookup(ProjectAssetManager.class);
        if(manager == null) return;
        FileObject file=context.getPrimaryFile();
        String assetName=manager.getRelativeAssetPath(file.getPath());
        SceneComposerTopComponent composer=SceneComposerTopComponent.findInstance();
        composer.addModel(manager.getManager(),assetName);
    }
}
