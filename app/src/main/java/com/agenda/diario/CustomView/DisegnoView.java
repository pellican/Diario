package com.agenda.diario.CustomView;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.agenda.diario.Database.Objet_to_File;
import com.agenda.diario.Note.OnDrawChangedListener;
import com.agenda.diario.Note.Pari;
import com.agenda.diario.Note.SPaint;
import com.agenda.diario.Note.SPath;

import java.util.ArrayList;

/**
 * Created by portatile on 09/08/2016.
 */
public class DisegnoView extends AppCompatImageView implements View.OnTouchListener {
    private static final float TOUCH_TOLERANCE = 4;
    private static final String TAG = "Disegno";
    public static final int STROKE = 0;
    public static final int ERASER = 1;
    public static final int FILL = 2;
    public static final int ZOOM_MODE = 3;
    public static final int CONTAGOCCE_S=7;
    public static final int CONTAGOCCE_F=8;
    public static final int DEFAULT_STROKE_SIZE = 5;
    public static final int DEFAULT_ERASER_SIZE = 50;
    public ProgressDialog pd;

    public static final int NONE = 4;
    public static final int DRAG = 5;
    public static final int ZOOM = 6;
    int modeZ = NONE;
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();
    PointF start = new PointF();
    PointF mid = new PointF();
    float oldDist = 1f;

    float scale= 1f;


    private float strokeSize = DEFAULT_STROKE_SIZE;
    private int strokeColor = Color.parseColor("#010000");
    private int fillColor = Color.parseColor("#010000");
    private float eraserSize = DEFAULT_ERASER_SIZE;
    private int background = Color.WHITE;

    final Point p1 = new Point();
    private SPath m_Path;
    private SPaint m_Paint;
    private float mX, mY;

    private int width, height;

    private ArrayList<Pari<SPath, SPaint>> paths = new ArrayList<>();
    private ArrayList<Pari<SPath, SPaint>> undonePaths = new ArrayList<>();

    private Objet_to_File objetToFile;

    private Context mContext;


    private Bitmap bitmap;
    private Bitmap newbitmap;

    private int mode = STROKE;

    private OnDrawChangedListener onDrawChangedListener;


    public DisegnoView(Context context, AttributeSet attr) {
        super(context, attr);

        this.mContext = context;
        pd = new ProgressDialog(context);
        objetToFile= new Objet_to_File(mContext);

        setFocusable(true);
        setFocusableInTouchMode(true);
        this.setOnTouchListener(this);
        setBackgroundColor(Color.WHITE);




        m_Paint = new SPaint();
        m_Paint.setAntiAlias(true);
        m_Paint.setDither(true);
        if (mode == FILL){
            m_Paint.setColor(fillColor);
        }else m_Paint.setColor(strokeColor);

        m_Paint.setStyle(Paint.Style.STROKE);
        m_Paint.setStrokeJoin(Paint.Join.ROUND);
        m_Paint.setStrokeCap(Paint.Cap.ROUND);
        m_Paint.setStrokeWidth(strokeSize);
        m_Path = new SPath();
        m_Paint.setShadowLayer(1, 0, 0, strokeColor);

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (bitmap != null) {
            canvas.drawBitmap(bitmap,matrix,null);
        }
        canvas.save();
        canvas.concat(matrix);
        for (Pari<SPath, SPaint> p : paths) {

            canvas.drawPath(p.getFirst(),p.getSecond());

        }
        canvas.restore();
        onDrawChangedListener.onDrawChanged();

    }

    public void setMode(int mode) {
            this.mode = mode;
    }


    public int getMode() {
        return this.mode;
    }


    private Bitmap getNewbitmap(){

        if (newbitmap == null) {
            newbitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        }
        newbitmap.eraseColor(background);
        Canvas canvas = new Canvas(newbitmap);
        if (bitmap != null) {
            canvas.drawBitmap(bitmap,0,0,null);
        }
        for (Pari<SPath, SPaint> p : paths) {

            canvas.drawPath(p.getFirst(), p.getSecond());
        }
        return newbitmap;
    }

    public Bitmap getBitmap() {

        if (paths.size() == 0)
            return null;

        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.eraseColor(background);
        }
        Canvas canvas = new Canvas(bitmap);
        for (Pari<SPath, SPaint> p : paths) {
            canvas.drawPath(p.getFirst(), p.getSecond());
        }

        return bitmap;
    }



    public void setBackgroundBitmap( Bitmap bitmap) {
        if (!bitmap.isMutable()) {
            android.graphics.Bitmap.Config bitmapConfig = bitmap.getConfig();
            // set default bitmap config if none
            if (bitmapConfig == null) {
                bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
            }
            bitmap = bitmap.copy(bitmapConfig, true);
        }
        this.bitmap = bitmap;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        width = View.MeasureSpec.getSize(widthMeasureSpec);
        height = View.MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }



    public boolean onTouch(View v, MotionEvent event) {

        float x = (event.getX()- myMatrix()[Matrix.MTRANS_X]) / myMatrix()[Matrix.MSCALE_X];
        float y = (event.getY()- myMatrix()[Matrix.MTRANS_Y]) / myMatrix()[Matrix.MSCALE_Y];

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:

                if (mode == FILL) {
                    flood_fill(x, y);
                    invalidate();
                }else if (mode == ZOOM_MODE){
                    savedMatrix.set(matrix);
                    start.set(event.getX(), event.getY());

                    modeZ = DRAG;
                    }
                else if (mode == STROKE || mode==ERASER ){
                    touch_start(x, y);
                    invalidate();
                }else if (mode == CONTAGOCCE_S){
                    strokeColor=getNewbitmap().getPixel((int)x,(int)y);
                    Toast.makeText(mContext,"Colore Catturato",Toast.LENGTH_SHORT).show();
                }else if (mode == CONTAGOCCE_F){
                    fillColor=getNewbitmap().getPixel((int)x,(int)y);
                    Toast.makeText(mContext,"Colore Catturato",Toast.LENGTH_SHORT).show();
                }
                break;

            case MotionEvent.ACTION_MOVE:

                if (mode == STROKE || mode == ERASER){
                    touch_move(x, y);
                    invalidate();
                }else if (mode == ZOOM_MODE){
                    if (modeZ == DRAG)
                    {
                        matrix.set(savedMatrix);
                        matrix.postTranslate(event.getX() - start.x,event.getY() - start.y );

                    }
                    else if (modeZ == ZOOM)
                    {  // pinch zooming
                        float newDist = spacing(event);
                        if (newDist > 5f)
                        {
                            matrix.set(savedMatrix);
                            scale = newDist / oldDist;
                            matrix.postScale(scale, scale, mid.x, mid.y);
                        }
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                if (mode == STROKE || mode==ERASER ){
                    touch_up();
                    invalidate();
                }
                break;

            case MotionEvent.ACTION_POINTER_UP:

                modeZ = NONE;
                Log.d(TAG, "mode=NONE");
                break;
            case MotionEvent.ACTION_POINTER_DOWN:

                oldDist = spacing(event);
                Log.d(TAG, "oldDist=" + oldDist);
                if (oldDist > 5f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    modeZ = ZOOM;
                    Log.d(TAG, "mode=ZOOM");
                }
                break;
            default:

        }
        if (mode == ZOOM_MODE){
            setImageMatrix(matrix);
            invalidate();
        }
        return true;
    }
      // zoom
    private float spacing(MotionEvent event)
    {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float)Math.sqrt(x * x + y * y);
    }
    private void midPoint(PointF point, MotionEvent event)
    {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    public void resetZoom(){
        scale=1f;
        matrix = new Matrix();
        matrix.setScale(1f, 1f);
        invalidate();
    }
    public float[] myMatrix(){
        float[] v = new float[9];
        matrix.getValues(v);
        return v;
    }





        // riempimento
    private void flood_fill(float x,float y){

        p1.x = (int) x;
        p1.y = (int) y;


        final int sourceColor = getNewbitmap().getPixel((int) x, (int) y);
        final int targetColor = fillColor;
        new TheTask(getNewbitmap(), p1, sourceColor, targetColor).execute();
    }



    private void touch_start(float x, float y) {
        // Clearing undone list
        undonePaths.clear();

        if (mode == ERASER) {
            m_Paint.setColor(Color.WHITE);
            m_Paint.setStrokeWidth(eraserSize);
        } else {
            m_Paint.setColor(strokeColor);
            m_Paint.setStrokeWidth(strokeSize);

        }

        // Avoids that a sketch with just erasures is saved
        if (!(paths.size() == 0 && mode == ERASER && bitmap == null)) {
            paths.add(new Pari<>(m_Path, new SPaint(m_Paint)));
        }

        m_Path.reset();
        m_Path.moveTo(x, y);
        mX = x;
        mY = y;
    }


    private void touch_move(float x, float y) {

        m_Path.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
        mX = x;
        mY = y;
    }


    private void touch_up() {

        m_Path.lineTo(mX, mY);
        // Avoids that a sketch with just erasures is saved
        if (!(paths.size() == 0 && mode == ERASER && bitmap == null)) {
            paths.add(new Pari<>(m_Path, new SPaint(m_Paint)));
        }
        // kill this so we don't double draw
        m_Path = new SPath();
    }





    public void undo() {

       if (paths.size() >= 2) {
            undonePaths.add(paths.remove(paths.size() - 1));
            // If there is not only one path remained both touch and move paths are removed
            undonePaths.add(paths.remove(paths.size() - 1));
            invalidate();
        }
    }


    public void redo() {
        if (undonePaths.size() > 0) {
            paths.add(undonePaths.remove(undonePaths.size() - 1));
            paths.add(undonePaths.remove(undonePaths.size() - 1));
            invalidate();
        }
    }


    public int getUndoneCount() {
        return undonePaths.size();
    }


    public ArrayList<Pari<SPath, SPaint>> getPaths() {
        return paths;
    }


    public void setPaths(ArrayList<Pari<SPath, SPaint>> paths) {
        this.paths = paths;
    }


    public ArrayList<Pari<SPath, SPaint>> getUndonePaths() {
        return undonePaths;
    }


    public void setUndonePaths(ArrayList<Pari<SPath, SPaint>> undonePaths) {
        this.undonePaths = undonePaths;
    }


    public int getStrokeSize() {
        return Math.round(this.strokeSize);
    }

    public int getFillColor() {
        return fillColor;
    }

    public void setFillColor(int fillColor) {
        this.fillColor = fillColor;
    }

    public void setSize(int size, int eraserOrStroke) {
        switch (eraserOrStroke) {
            case STROKE:
                strokeSize = size;
                break;
            case ERASER:
                eraserSize = size;
                break;
            default:
                //Log.e(Constants.TAG, "Wrong element choosen: " + eraserOrStroke);
        }

    }

    public void scriviListToFile(){
        if (paths.size() ==0){
            paths.addAll(leggiListDaFile());
            invalidate();
        }else objetToFile.scriviListToFile(paths);

    }
    public ArrayList<Pari<SPath, SPaint>> leggiListDaFile(){
        return objetToFile.leggiListDaFile();
    }

    public int getStrokeColor() {
        return this.strokeColor;
    }


    public void setStrokeColor(int color) {

        strokeColor = color;
    }


    public void erase() {
        paths.clear();
        undonePaths.clear();
        invalidate();
    }


    public void setOnDrawChangedListener(OnDrawChangedListener listener) {
        this.onDrawChangedListener = listener;
    }

    class TheTask extends AsyncTask<Void, Integer, Void> {

        Bitmap bmp;
        Point pt;
        int replacementColor,targetColor;

        public TheTask(Bitmap bm,Point p, int sc, int tc)
        {
            this.bmp=bm;
            this.pt=p;
            this.replacementColor=tc;
            this.targetColor=sc;
            pd.setMessage("Filling....");
            pd.show();
        }
        @Override
        protected void onPreExecute() {
            pd.show();

        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }

        @Override
        protected Void doInBackground(Void... params) {
            FloodFill f= new FloodFill();
            m_Path= f.floodFill(bmp,pt,targetColor,replacementColor);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            m_Paint.setColor(fillColor);
            m_Paint.setShadowLayer(2,0,0,fillColor);
            m_Paint.setStrokeWidth(2);
            paths.add(new Pari<>(m_Path, new SPaint(m_Paint)));
            paths.add(new Pari<>(m_Path, new SPaint(m_Paint)));
            m_Paint.setShadowLayer(1,0,0,strokeColor);
            m_Path = new SPath();
            pd.dismiss();
            invalidate();

        }
    }

}
