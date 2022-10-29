/*
 * NOSA HEADER START
 *
 * The contents of this file are subject to the terms of the NASA Open
 * Source Agreement (NOSA), Version 1.3 only (the "Agreement").  You may
 * not use this file except in compliance with the Agreement.
 *
 * You can obtain a copy of the agreement at
 *   docs/NASA_Open_Source_Agreement_1.3.txt
 * or
 *   http://sscweb.gsfc.nasa.gov/tipsod/NASA_Open_Source_Agreement_1.3.txt.
 *
 * See the Agreement for the specific language governing permissions
 * and limitations under the Agreement.
 *
 * When distributing Covered Code, include this NOSA HEADER in each
 * file and include the Agreement file at
 * docs/NASA_Open_Source_Agreement_1.3.txt.  If applicable, add the
 * following below this NOSA HEADER, with the fields enclosed by
 * brackets "[]" replaced with your own identifying information:
 * Portions Copyright [yyyy] [name of copyright owner]
 *
 * NOSA HEADER END
 *
 * Copyright (c) 2003-2009 United States Government as represented by the
 * National Aeronautics and Space Administration. No copyright is claimed
 * in the United States under Title 17, U.S.Code. All Other Rights Reserved.
 *
 * $Id: StarsPlanetsMoons.java,v 1.3 2017/03/06 20:05:00 rchimiak Exp $
 */
package gov.nasa.gsfc.spdf.orb.content.shapes;

import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.image.TextureLoader;
import java.awt.Toolkit;
import javax.media.j3d.Appearance;
import javax.media.j3d.Geometry;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.Material;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Texture;
import javax.media.j3d.TextureAttributes;
import javax.vecmath.Color3f;

/**
 * The Earth class represents the earth as a Sphere extention with added texture
 *
 * @author rchimiak
 * @version $Revision: 1.3 $
 */
public abstract class StarsPlanetsMoons extends Sphere {

    /**
     * Creates new earth using the TextureLoader.
     *
     * @param scale determine the size of the radius
     */
    private Material mat = null;
    private static final float EMISSIVE_RED = .7f;
    private static final float EMISSIVE_GREEN = .7f;
    private static final float EMISSIVE_BLUE = .7f;
    private static final float DIFFUSE_RED = .8f;
    private static final float DIFFUSE_GREEN = .8f;
    private static final float DIFFUSE_BLUE = .8f;
    private static final float SPECULAR_RED = .8f;
    private static final float SPECULAR_GREEN = .8f;
    private static final float SPECULAR_BLUE = .8f;
    private static final int NUMBER_OF_DIVISIONS = 300;
    private static final float SHININESS_INTENSITY = 2.5f;

    public StarsPlanetsMoons(double scale, String resource) {

        super((float) (1 / scale), Primitive.GEOMETRY_NOT_SHARED + Primitive.GENERATE_TEXTURE_COORDS + Primitive.GENERATE_NORMALS, NUMBER_OF_DIVISIONS, new Appearance());

        getShape().setCapability(Shape3D.ALLOW_APPEARANCE_READ);
        getShape().setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
        getShape().setCapability(Shape3D.ALLOW_GEOMETRY_READ);
        getShape().setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
        getShape().setCapability(Shape3D.ALLOW_BOUNDS_READ);
        getShape().getGeometry().setCapability(Geometry.ALLOW_INTERSECT);
        getAppearance().setCapability(Appearance.ALLOW_MATERIAL_READ);
        getAppearance().setCapability(Appearance.ALLOW_MATERIAL_WRITE);

        TextureLoader loader = new TextureLoader(Toolkit.getDefaultToolkit().
                getImage(Earth.class.getResource(resource)), null);

        ImageComponent2D image = loader.getImage();

        if (image == null) {
            String body = resource.substring(resource.lastIndexOf("/") + 1, resource.lastIndexOf("."));
            System.out.println("load failed for image: " + body);
            return;
        }

        Texture texture = loader.getTexture();

        texture.setEnable(true);

        image.getImage().flush();

        getAppearance().setTexture(texture);
      
    }

    protected void setLightingMaterial() {

        mat = new Material();

        Color3f emissive = new Color3f(EMISSIVE_RED, EMISSIVE_GREEN, EMISSIVE_BLUE);

        Color3f diffuseColor = new Color3f(DIFFUSE_RED, DIFFUSE_GREEN, DIFFUSE_BLUE);

        Color3f specularColor = new Color3f(SPECULAR_RED, SPECULAR_GREEN, SPECULAR_BLUE);

        mat.setEmissiveColor(emissive);
        mat.setSpecularColor(specularColor);
        mat.setDiffuseColor(diffuseColor);
        mat.setShininess(SHININESS_INTENSITY);
        mat.setLightingEnable(true);

    }

    public void setSunLight(final boolean sunlight) {

        if (sunlight) {
            getAppearance().setMaterial(mat);
        } else {
            getAppearance().setMaterial(null);
        }
    }

}
