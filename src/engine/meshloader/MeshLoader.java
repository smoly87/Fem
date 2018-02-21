/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.meshloader;

import engine.Mesh;

/**
 *
 * @author Andrey
 */
public abstract class MeshLoader {
    public abstract Mesh loadMesh(String fileName);
}
