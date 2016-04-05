package cn.edu.nju.aucminer.recommender;

import java.util.ArrayList;
import java.util.List;

import cn.edu.nju.aucminer.client.adaptionexamples.ApiAdaptionExample;

public class DefaultAPIReplacementSearch implements IAPIReplacementSearcher {

	@Override
	public APIReplacementSearchResult findReplacementForAPI(List<MethodInfo> oldMethods) {
		String oldMethodName = oldMethods.get(0).getFullQualifiedName();
		List<MethodInfo> newMethods = new ArrayList<>();
		List<ApiAdaptionExample> examples = new ArrayList<>();
		if (oldMethodName.contains("LayoutParams.FILL_PARENT")) {
			newMethods.add(new MethodInfo("int#android.view.ViewGroup.LayoutParams.MATCH_PARENT"));
		}
		else if (oldMethodName.contains("Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET")) {
			newMethods.add(new MethodInfo("int#android.content.Intent.FLAG_ACTIVITY_NEW_DOCUMENT"));
		}
		else if (oldMethodName.contains("Build.VERSION.SDK")) {
			newMethods.add(new MethodInfo("int#android.os.Build.VERSION.SDK_INT"));
		}
		else if (oldMethodName.contains("PixelFormat.YCbCr_420_SP")) {
			newMethods.add(new MethodInfo("int#android.graphics.ImageFormat.NV21"));
		}
		else if (oldMethodName.contains("PixelFormat.YCbCr_422_SP")) {
			newMethods.add(new MethodInfo("int#android.graphics.ImageFormat.NV16"));
		}
		else if (oldMethodName.contains("Display.getWidth")) {
			newMethods.add(new MethodInfo("void#android.view.Display.getSize(android.graphics.Point)"));
		}
		else if (oldMethodName.contains("Display.getHeight")) {
			newMethods.add(new MethodInfo("void#android.view.Display.getSize(android.graphics.Point)"));
		}
		else if (oldMethodName.contains("ViewTreeObserver.removeGlobalOnLayoutListener")) {
			newMethods.add(new MethodInfo("void#android.view.ViewTreeObserver.removeOnGlobalLayoutListener(android.view.ViewTreeObserver.OnGlobalLayoutListener)"));
		}
		else if (oldMethodName.contains("BitmapDrawable.BitmapDrawable()")) {
			newMethods.add(new MethodInfo("#android.graphics.drawable.BitmapDrawable.BitmapDrawable(android.content.res.Resources, android.graphics.Bitmap)"));
		}
		else if (oldMethodName.contains("android.os.StatFs.getBlockSize()")) {
			newMethods.add(new MethodInfo("long#android.os.StatFs.getBlockSizeLong()"));
		}
		else if (oldMethodName.contains("android.os.StatFs.getAvailableBlocks()")) {
			newMethods.add(new MethodInfo("long#android.os.StatFs.getAvailableBlocksLong()"));
		}
		else if (oldMethodName.contains("View.setBackgroundDrawable")) {
			newMethods.add(new MethodInfo("void#android.view.View.setBackground(android.graphics.drawable.Drawable)"));
			examples.add(new ApiAdaptionExample("C:\\Users\\fei\\Desktop\\1.java", 2160, 2160, "C:\\Users\\fei\\Desktop\\2.java", 4103, 4103));
		}
		else if (oldMethodName.contains("android.util.FloatMath.sin")) {
			newMethods.add(new MethodInfo("double#java.lang.Math.sin(double)"));
		}
		else if (oldMethodName.contains("android.content.res.Resources.getDrawable(int)")) {
			newMethods.add(new MethodInfo("Drawable#android.content.res.Resources.getDrawable(int, android.content.res.Resources.Theme) "));
		}
		else {
			return null;
		}
		return new APIReplacementSearchResult(newMethods, examples); 
	}

}
