package de.palmen_it.android.wsm.hidedockiconstext;

import java.lang.reflect.Method;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class LoadPackageHook implements IXposedHookLoadPackage {

    private static void log(String log) {
        XposedBridge.log("[HideDockIconsText] " + log);
    }

	@Override
	public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
        if (lpparam.packageName.equals("com.miui.home")) {
            log("Start hook in: " + lpparam.packageName);
            ClassLoader classLoader = lpparam.classLoader;
            try {
                    Class.forName("com.miui.home.launcher.ShortcutIcon", false, classLoader);
                    log("Searching for methods...");
                    XposedHelpers.findAndHookMethod("com.miui.home.launcher.ShortcutIcon", classLoader,
                                    "onFinishInflate", new XC_MethodHook() {
                                            @Override
                                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                                    FrameLayout v = (FrameLayout) param.thisObject;
                                                    // dumpProperties(v);
                                                    Method lp = v.getClass().getMethod("getLayoutParams");
                                                    if (lp == null) return;
                                                    if (lp.invoke(v).toString().startsWith("android.view.ViewGroup$LayoutParams"))
                                                    {
                                                    	final Context context = v.getContext();
                                                    	int resID = context.getResources().getIdentifier("icon_title", "id", "com.miui.home");
                                                    	v.findViewById(resID).setVisibility(View.INVISIBLE);
                                                    }
                                            }
                    });
            } catch (ClassNotFoundException ignored) {
                    log("Class not found! Skipping...");
            } catch (NoSuchMethodError ignored) {
                    log("Method not found! Skipping...");
            }
        }
    }
}
