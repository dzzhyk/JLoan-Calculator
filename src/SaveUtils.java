import java.util.LinkedList;
import java.util.List;

/**
 * 计算历史工具类
 * @author dzzhyk
 */
public class SaveUtils {
    private static final LinkedList<CalculatorBean> beans = new LinkedList<>();

    public static void saveBean(CalculatorBean bean){
        beans.add(bean);
    }

    public static List<CalculatorBean> getBeans(){
        return beans;
    }
}
