package com.bolo.util;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.FileReader;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.FileReader;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JSUtil {

    protected static Logger logger = LoggerFactory.getLogger("Util");
    private static final Map<String, String> cacheJsFile = new ConcurrentHashMap<String, String>();
    /*public static Object executeJsFuncWithEnv(String jsFuncName, String envjs, String jsFile, Object... args) throws Exception {

        Context context = ContextFactory.getGlobal().enterContext();
        context.setOptimizationLevel(-1);
        context.setLanguageVersion(Context.VERSION_1_7);
        ScriptableObject scriptableObject = context.initStandardObjects();

        // Create the print function
        String printFunction = "function print(message) { java.lang.System.out.println(message); }";
        context.evaluateString(scriptableObject, printFunction, "print", 1, null);

        // Assumes we have env.rhino.js as a resource on the classpath.
        loadJavaScriptFiles(context, scriptableObject, envjs);
        loadJavaScriptFiles(context, scriptableObject, jsFile);
        loadJavaScriptUrls(//
                "http://ajax.googleapis.com/ajax/libs/angularjs/1.0.3/angular.min.js", //
                "http://code.jquery.com/jquery-1.8.2.js", //
                "http://code.jquery.com/ui/1.9.1/jquery-ui.js", //
                "http://www.google-analytics.com/ga.js", //
                "http://maps.google.com/maps/api/js?sensor=false");
        StringBuilder sb = new StringBuilder(jsFuncName).append("(");
        int i = 0;
        for(Object arg : args) {
            if ( i > 0) {
                sb.append(",");
            }
            sb.append("'" + arg + "'");
            i++;
        }
        sb.append(");");
        logger.error(sb.toString());
        Object result = context.evaluateString(scriptableObject, sb.toString() , "execute", 1, null);
        return result;
        // This will load the home page DOM.
        // run("window.location='http://81.95.118.139/ikube/search.jsp'");
    }
    public static String executeJsFuncWithEnvJs(String jsFuncName, String envjs, String jsFile, Object... args) {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByExtension("js");
        try {
            // 执行指定脚本
            File file = new File(envjs);
            if (file.isDirectory()) {
                for(File f1 : file.listFiles()) {
                    logger.error(f1.getAbsolutePath());
                    engine.eval(FileUtils.readFileToString(f1));
                }
            } else {
                engine.eval(FileUtils.readFileToString(file));
            }
            file = new File(jsFile);
            engine.eval(FileUtils.readFileToString(file));
            if(engine instanceof Invocable) {
                Invocable invoke = (Invocable)engine;
                Object c = (Object)invoke.invokeFunction(jsFuncName, args);
                return c.toString();
            }
        } catch (Exception e1) {
            logger.error("envjs", e1);
        } finally {
            //IOUtils.closeQuietly(reader);
        }
        return null;
        Context context = ContextFactory.getGlobal().enterContext();
        context.setOptimizationLevel(-1);
        context.setLanguageVersion(Context.VERSION_1_8);
        ScriptableObject scriptableObject = context.initStandardObjects();

        // Create the print function
        String printFunction = "function print(message) { java.lang.System.out.println(message); }";
        context.evaluateString(scriptableObject, printFunction, "print", 1, null);

        // Assumes we have env.rhino.js as a resource on the classpath.
        loadJavaScriptFiles("env.rhino");
        loadJavaScriptUrls(//
                "http://ajax.googleapis.com/ajax/libs/angularjs/1.0.3/angular.min.js", //
                "http://code.jquery.com/jquery-1.8.2.js", //
                "http://code.jquery.com/ui/1.9.1/jquery-ui.js", //
                "http://www.google-analytics.com/ga.js", //
                "http://maps.google.com/maps/api/js?sensor=false");
        Object result = context.evaluateString(scriptableObject, jsonParse, "parse", 1, null);

        // This will load the home page DOM.
        // run("window.location='http://81.95.118.139/ikube/search.jsp'");
    }*/
    public static String executeJsFunc(String jsFuncName, String jsFile, Object... args) {
        return executeJsFunc1(jsFuncName, false, jsFile, args);
    }
    public static String executeResourceJsFunc(String jsFuncName, String jsFile, Object... args) {
        return executeJsFunc1(jsFuncName, true, jsFile, args);
    }
    private static Map<String, ScriptEngine> engineMap = new ConcurrentHashMap<>();
    private static String executeJsFunc1(String jsFuncName, boolean classPathRes, String jsFiles, Object... args) {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = null;
        manager.getEngineByExtension("js");
        FileReader reader = null;
        InputStream is = null;
        try {
            engine = engineMap.get(jsFiles);
            if (engine == null) {
                engine = manager.getEngineByExtension("js");
                String[] jsFileArr = jsFiles.split(",");
                for (String jsFile : jsFileArr) {
                    String js = cacheJsFile.get(jsFile);
                    if (js == null) {
                        if (classPathRes) {
                            is = JSUtil.class.getResourceAsStream(jsFile);
                            js = IOUtils.toString(is, "utf-8");
                        } else {
                            reader = new FileReader(jsFile);
                            js = IOUtils.toString(reader);
                        }
                        cacheJsFile.put(jsFile, js);
                    }
                    engine.eval(js);
                }
                engineMap.put(jsFiles, engine);
            }
            if(engine instanceof Invocable) {
                Invocable invoke = (Invocable)engine;
                Object c = invoke.invokeFunction(jsFuncName, args);
                return c.toString();
            }
        } catch (Exception e1) {
            logger.error("execJsFunc", e1);
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(reader);
        }
        return null;
    }
    public static String executeJsFuncForDebug(String jsFuncName, String jsFiles, Object... args) {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = null;
        manager.getEngineByExtension("js");
        FileReader reader = null;
        InputStream is = null;
        try {
            engine = engineMap.get(jsFiles);
            if (engine == null) {
                engine = manager.getEngineByExtension("js");
                String[] jsFileArr = jsFiles.split(",");
                for (String jsFile : jsFileArr) {
                    engine.eval(String.format("load('%s')", jsFile));
                }
                engineMap.put(jsFiles, engine);
            }
            if(engine instanceof Invocable) {
                Invocable invoke = (Invocable)engine;
                Object c = invoke.invokeFunction(jsFuncName, args);
                return c.toString();
            }
        } catch (Exception e1) {
            logger.error("execJsFunc", e1);
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(reader);
        }
        return null;
    }
    private static String evalJs(String eval, String... js) {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByExtension("js");
        FileReader reader = null;
        try {
            // 执行指定脚本
            for(String j : js) {
                engine.eval(j);
            }
            Object obj = engine.eval(eval);
            //System.out.println(engine.get("Two4TwoSeven"));
            return obj.toString();
        } catch (Exception e1) {
            logger.error("evalJs", e1);
        } finally {
            IOUtils.closeQuietly(reader);
        }
        return null;
    }
	/*private void loadJavaScriptUrls(Context context, final String... urls) throws Exception {
		for (final String urlString : urls) {
			URL url = new URL(urlString);
			String javaScript = FileUtilities.getContents(url.openStream(), Integer.MAX_VALUE).toString();
			context.evaluateString(scriptableObject, javaScript, url.getFile(), 1, null);
		}
	}*/

	/*private static void loadJavaScriptFiles(Context context, ScriptableObject scriptableObject, final String envjs) throws IOException {
		File file = new File(envjs);
		if (file.isDirectory()) {
			for(File f1 : file.listFiles()) {
				logger.error(f1.getAbsolutePath());
				String javaScript = FileUtils.readFileToString(f1);
				context.evaluateString(scriptableObject, javaScript, f1.getAbsolutePath(), 1, null);
			}
		} else {
			//logger.error(file);
			String javaScript = FileUtils.readFileToString(file);
			context.evaluateString(scriptableObject, javaScript, file.getAbsolutePath(), 1, null);
		}
	}*/

    /**
     * @param args
     */
    public static void main(String[] args) {
        //System.out.println(evalJs("aa('d=D^C;i=B^E;o=F^A;a=H^G;n=8;j=0;s=J^y;p=9;q=u^x;r=7;h=1;k=2;b=3;m=z^w;l=v^I;t=6;f=U^T;g=V^M;e=5;c=4;K=j^l;Q=h^i;P=k^g;O=b^a;N=c^d;L=e^m;R=t^s;S=r^q;X=n^o;W=p^f;',60,60,'^^^^^^^^^^Nine0Three^Three^Six^Zero6One^Seven^Six2Nine^Six3Zero^One^EightZeroFour^Nine^Five^OneZeroEight^NineNineSix^Zero^Five7Five^Two^NineSixTwo^Four^Eight3Seven^Eight^4073^795^443^8118^8080^4388^8000^5689^8085^10812^80^1332^3129^5825^88^1615^One5ZeroThree^FourZeroFiveFive^1337^Four8OneTwo^SixNineNineZero^Zero0SevenOne^Zero6EightNine^SixSevenFourEight^FourEightSixSeven^8090^10023^5805^Four9ThreeSix^Zero3TwoFour'.split('\u005e'),0,{})", "function aa(p,r,o,x,y,s){y=function(c){return(c<r?'':y(parseInt(c/r)))+((c=c%r)>35?String.fromCharCode(c+29):c.toString(36))};if(!''.replace(/^/,String)){while(o--){s[y(o)]=x[o]||y(o)}x=[function(y){return s[y]}];y=function(){return'\\w+'};o=1};while(o--){if(x[o]){p=p.replace(new RegExp('\\b'+y(o)+'\\b','g'),x[o])}}return p}"));
        System.out.println(evalJs("(Two4TwoSeven^Nine7Six)+(ThreeEightEightThree^NineEightSeven)+(SevenZeroSevenEight^TwoSixOne)+(SevenEightZeroNine^EightEightThree)", "eval(function(p,r,o,x,y,s){y=function(c){return(c<r?'':y(parseInt(c/r)))+((c=c%r)>35?String.fromCharCode(c+29):c.toString(36))};if(!''.replace(/^/,String)){while(o--){s[y(o)]=x[o]||y(o)}x=[function(y){return s[y]}];y=function(){return'\\w+'};o=1};while(o--){if(x[o]){p=p.replace(new RegExp('\\b'+y(o)+'\\b','g'),x[o])}}return p}('g=B^C;d=G^I;t=F^E;j=H^J;l=K^u;k=1;o=7;p=v^w;s=6;h=A^z;r=y^x;a=3;e=5;n=8;b=D^V;i=0;f=2;q=9;c=4;m=O^X;L=i^j;Q=k^g;P=f^b;M=a^m;N=c^d;R=e^l;S=s^r;W=o^p;U=n^t;T=q^h;',60,60,'^^^^^^^^^^Seven^TwoSixOne^Two^One2Zero^Nine^Six^NineEightSeven^FourOneEight^One^OneSixTwo^Four^FourZeroFive^Nine7Six^Eight^Three^FourFourFour^Five^TwoZeroNine^Zero^EightEightThree^8090^314^8088^9090^4099^8000^2897^10491^1080^10011^8085^3475^8997^945^88^1337^12169^ZeroSevenSixFour^Two4TwoSeven^Four7NineTwo^9483^SevenZeroSevenEight^ThreeEightEightThree^EightNineOneFive^Zero6FourOne^Nine2ThreeZero^SevenEightZeroNine^81^Two3FiveSix^8888'.split('\u005e'),0,{}))"));
        //System.out.println(evalJs("(Two4TwoSeven^Nine7Six)+(ThreeEightEightThree^NineEightSeven)+(SevenZeroSevenEight^TwoSixOne)+(SevenEightZeroNine^EightEightThree)", "function aa(p,r,o,x,y,s){y=function(c){return(c<r?'':y(parseInt(c/r)))+((c=c%r)>35?String.fromCharCode(c+29):c.toString(36))};if(!''.replace(/^/,String)){while(o--){s[y(o)]=x[o]||y(o)}x=[function(y){return s[y]}];y=function(){return'\\w+'};o=1};while(o--){if(x[o]){p=p.replace(new RegExp('\\b'+y(o)+'\\b','g'),x[o])}}return p}"));

    }

}
