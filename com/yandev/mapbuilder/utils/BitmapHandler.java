package com.yandev.mapbuilder.utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BitmapHandler {
    private final Map<String, BufferedImage> bitmapList = new HashMap<>();
    private static BitmapHandler bitmapHandler;
    public static BitmapHandler getInstance() {
        if(bitmapHandler == null) bitmapHandler = new BitmapHandler();
        return bitmapHandler;
    }

    // currently 32x32
    public List<BufferedImage> cropToTiles(int pixelSize, BufferedImage tileBitmap, int resizedTileSize) {
        List<BufferedImage> tiles = new ArrayList<>();
        try {

            if (tileBitmap.getWidth()%pixelSize  != 0  || tileBitmap.getHeight()%pixelSize != 0  ) {
                throw new IOException();
            }
            int tilesOnWidth = tileBitmap.getWidth() / pixelSize;
            int tilesOnHeight = tileBitmap.getHeight() / pixelSize;
            // parse tiles from input Image
            for (int i = 0; i < tilesOnHeight; i++) {
                for(int j = 0; j < tilesOnWidth; j++) {
                    int xCoordinate = j*pixelSize;
                    int yCoordinate = i*pixelSize;
                    BufferedImage bitmap = tileBitmap.getSubimage(xCoordinate, yCoordinate, pixelSize , pixelSize);
                    BufferedImage preparedImage = resize(bitmap, resizedTileSize , resizedTileSize);
                    tiles.add(preparedImage);
                }
            }

        } catch(IOException e){
            System.out.println("tiles size are wrong");
        }
        return tiles;
    }

    public static BufferedImage resize(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = dimg.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }
  /*  public  Map<String , BufferedImage> getBitmapElements(){
        if(bitmapList.size() == 0) {
            TypedArray typedArray = context.getResources().obtainTypedArray(R.array.drawable_array);
            for (int i = 0; i < typedArray.length(); i++) {
                int resourceId = typedArray.getResourceId(i, 0);
                Bitmap tempBitmap = BitmapFactory.decodeResource(context.getResources(), resourceId);
                Bitmap tempConvertedBitmap = tempBitmap.copy(Bitmap.Config.ARGB_8888, false);
                bitmapList.put(context.getResources().getResourceEntryName(resourceId), tempConvertedBitmap);
            }
            typedArray.recycle();
        }
        return bitmapList;
    }*/

}