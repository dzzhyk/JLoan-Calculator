/**
 * 计算标准类
 * @author dzzhyk
 */
public class CalculatorBean {
    // 计算方式
    public String areaOrTotal;
    // 单价
    public Double price;
    // 面积
    public Double area;
    // 房款总额
    public Double total_price;
    // 按揭成数
    public Integer mortgage_ratio;
    // 还款方式
    public Integer type;
    // 商贷总额
    public Double loan;
    // 公积金总额
    public Double fund;
    // 贷款总额
    public Double loan_amount;
    // 贷款月数
    public Integer mortgage_month;
    // 还款总额
    public Double totalPriceHuankuan;
    // 商贷利率
    public Double loan_ratio;
    // 公积金利率
    public Double fund_ratio;
    // 支付利息款
    public Double totalInterest;
    // 首期付款
    public Double down_payment;
    // 每月还款
    public Double pricePerMonth;
    // 平均值（公积金方式）
    public Double[] monthPaymentList;
}
