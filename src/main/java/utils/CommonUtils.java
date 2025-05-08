package utils;

import java.awt.Point;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import shapes.Shape;
import shapes.System_Obj;

public class CommonUtils {

    public static Point screenToWorld(int x, int y, double zoomFactor, double offsetX, double offsetY) {
        return new Point(
                (int) ((x / zoomFactor) - offsetX),
                (int) ((y / zoomFactor) - offsetY));
    }

    public static Point worldToScreen(Point worldPoint, double zoomFactor, double offsetX, double offsetY) {
        return new Point(
                (int) ((worldPoint.x + offsetX) * zoomFactor),
                (int) ((worldPoint.y + offsetY) * zoomFactor));
    }

    public static double getNearestHalf(double d) {
        /*
         * 0 -> 0
         * 0.1 -> 0
         * ...
         * 0.4 -> .5
         * 0.5 -> .5
         * 0.6 -> .5
         * ...
         * 0.9 -> 1
         * 1 -> 1
         * 1.1 -> 1
         */
        return Math.round(d * 2) / 2.0;
    }

}
