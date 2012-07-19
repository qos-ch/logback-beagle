/**
 * Logback-beagle: The logback Console Plugin for Eclipse 
 * Copyright (C) 2006-2012, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package ch.qos.logback.beagle.util;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import ch.qos.logback.beagle.Constants;

public class ResourceUtil {

  public static Image ERROR_IMG;
  public static Image WARN_IMG;

  public static String ERROR_IMG_KEY = "ERROR_IMG_KEY";
  public static String WARN_IMG_KEY = "WARN_IMG_KEY";
  public static String RELEASE_SCROLL_LOCK_IMG_KEY = "RELEASE_SCROLL_LOCK_IMG_KEY";
  public static String EXPAND_CALLERS_IMG_KEY = "EXPAND_CALLERS_IMG_KEY";
  public static String COPY_CLIPBAORD_IMG_KEY = "COPY_CLIPBAORD_IMG_KEY";
  
  public static String JUMP_IMG_KEY = "JUMP_IMG_KEY";
  public static Color GRAY;
  public static Font FONT;

  static String PATH_TO_ICONS = "icons/";

  private static Map<String, Image> IMAGE_MAP = new HashMap<String, Image>();

  public static void init(Display display) {

    WARN_IMG = loadImage(display, PATH_TO_ICONS + "flag_orange.png");
    ERROR_IMG = loadImage(display, PATH_TO_ICONS + "flag_red.png");

    putImage(WARN_IMG_KEY, WARN_IMG);
    putImage(ERROR_IMG_KEY, ERROR_IMG);

    putImage(RELEASE_SCROLL_LOCK_IMG_KEY,
	loadImage(display, PATH_TO_ICONS + "play_doc.gif"));

    putImage(JUMP_IMG_KEY, loadImage(display, PATH_TO_ICONS + "book_open.png"));
    putImage(EXPAND_CALLERS_IMG_KEY,
	loadImage(display, PATH_TO_ICONS + "preview-16x16.png"));
    putImage(COPY_CLIPBAORD_IMG_KEY,
	loadImage(display, PATH_TO_ICONS + "copy-16x16.png"));

    FONT = new Font(null, "Courier", 10, SWT.NORMAL);
    GRAY = new Color(display, 245, 247, 248);

  }

  public static Image loadImage(Display display, String resourcePath) {
    // URL url = ResourceUtil.class.getClassLoader().getResource(resourcePath);
    // ImageDescriptor iDescriptor = ImageDescriptor.createFromURL(url);
    ImageDescriptor iDescriptor = ImageDescriptor.createFromFile(
	Constants.class,
	resourcePath);
    return iDescriptor.createImage();
  }

  public static Image loadScaledImage(Display display, String resourcePath,
      int scale) {
    Image image = loadImage(display, resourcePath);
    Image scaled = new Image(display, image.getImageData().scaledTo(scale,
	scale));
    return scaled;
  }

  public static void putImage(String key, Image img) {
    Image old = IMAGE_MAP.get(key);
    if (old != null) {
      old.dispose();
    }
    IMAGE_MAP.put(key, img);
  }

  public static Image getImage(String key) {
    return IMAGE_MAP.get(key);
  }

  public static void dispose() {
    for (Image img : IMAGE_MAP.values()) {
      if (img != null) {
	img.dispose();
      }
    }
    FONT.dispose();
    GRAY.dispose();
  }
}
