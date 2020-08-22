package com.agenda.diario.CustomView;

import android.graphics.Bitmap;
import android.graphics.Path;
import android.graphics.Point;

import com.agenda.diario.Note.SPath;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by portatile on 10/08/2016.
 */
public class FloodFill {
    public SPath  floodFill(Bitmap image, Point node, int targetColor,
                          int replacementColor) {
        SPath path= new SPath();
        int width = image.getWidth();
        int height = image.getHeight();


        if (targetColor != replacementColor) {
            Queue<Point> queue = new LinkedList<>();
            do {
              //  path.reset();
                int x = node.x;
                int y = node.y;
                path.moveTo((float)node.x,(float)node.y);

                while (x > 0 && image.getPixel(x - 1, y) == targetColor) {
                    x--;
                }

                boolean spanUp = false;
                boolean spanDown = false;
                while (x < width && image.getPixel(x, y) == targetColor) {
                    image.setPixel(x, y, replacementColor);
                    path.lineTo((float)x,(float)y);
                    if (!spanUp && y > 0
                            && image.getPixel(x, y - 1) == targetColor) {
                        queue.add(new Point(x, y - 1));
                        spanUp = true;
                    } else if (spanUp && y > 0
                            && image.getPixel(x, y - 1) != targetColor) {
                        spanUp = false;
                    }
                    if (!spanDown && y < height - 1
                            && image.getPixel(x, y + 1) == targetColor) {
                        queue.add(new Point(x, y + 1));
                        spanDown = true;
                    } else if (spanDown && y < (height - 1)
                            && image.getPixel(x, y + 1) != targetColor) {
                        spanDown = false;
                    }
                    x++;
                }

            } while ((node = queue.poll()) != null);
        }
        return path;
    }
}
