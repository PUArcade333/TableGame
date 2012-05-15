package puArcade.princetonTD.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

public class StringResources {
	
	public static Bitmap toBitmap(Context context, String name)
	{
		return BitmapFactory.decodeResource(context.getResources(), toID(context, name));
	}
	
	public static Drawable toDrawable(Context context, String name)
	{
		return context.getResources().getDrawable(toID(context, name));
	}
	
	public static int toID(Context context, String name)
	{
		return context.getResources().getIdentifier(name, "drawable", context.getPackageName());
	}
}
