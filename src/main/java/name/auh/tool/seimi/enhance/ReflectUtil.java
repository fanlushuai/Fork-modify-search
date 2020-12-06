package name.auh.tool.seimi.enhance;

import org.reflections8.Reflections;

public class ReflectUtil {

    public static String getPrePartPackageName(Class cls, int prePart) {
        //保证就算类被拷贝走也不用修改直接用
        String[] splitPackageName = cls.getPackage().getName().split("\\.");
        StringBuffer reflectPackageName = new StringBuffer();
        for (int i = 0; i < splitPackageName.length; i++) {
            String s = splitPackageName[i];
            reflectPackageName.append(s);
            if (i == prePart) {
                return reflectPackageName.toString();
            }
            reflectPackageName.append(".");
        }
        return "傻叉";
    }

    public static Reflections getReflections(String packageName) {
        return new Reflections(packageName);
    }


}
