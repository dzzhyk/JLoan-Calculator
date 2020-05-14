/**
 * @author dzzhyk
 * 贷款计算工具类
 */
public class CalculatorUtils {
    //还款方式
    public static class REFUND_TYPE {
        public static int AVERAGE_PRINCIPAL = 1;    //等额本息
        public static int AVERAGE_CAPITAL = 2;      //等额本金
    }

    //贷款类别
    public static class LOAN_TYPE {
        public static int LOAN = 1;             //商贷
        public static int FUND = 2;             //公积金
        public static int PORTFOLIO = 3;        //组合贷
    }

    //计算方式
    public static class CALCULATION_TYPE {
        public static int TOTAL_LOANS = 1;          // 根据贷款总额
        public static int UNIT_PRICE_AND_AREA = 2;  // 根据单价和面积
    }

    /**
     *
     * @param refund_type       还款方式
     * @param mortgage_month    贷款月数
     * @param loan_interest     商贷利率
     * @param fund_interest     公积金利率
     * @param shangyexing       商贷
     * @param gongjijin         公积金
     * @return bean
     */
    //计算 组合贷
    public static CalculatorBean calcPortfolio(int refund_type, int mortgage_month, double loan_interest, double fund_interest, double shangyexing, double gongjijin) {
        return coreCalculate(refund_type, LOAN_TYPE.PORTFOLIO, mortgage_month, 0, 0, 0, 0, loan_interest, fund_interest, 0, shangyexing, gongjijin);
    }

    /**
     *
     * @param refund_type       还款方式
     * @param loan_type         贷款类型
     * @param mortgage_month    贷款月数
     * @param loan_totalprice_type  计算方式
     * @param single_price          单价
     * @param area                  面积
     * @param mortgage_ratio        按揭成数
     * @param loan_interest         商贷利率
     * @param fund_interest         公积金利率
     * @param total_price           房屋总价 = 单价 * 面积
     * @return bean
     */
    //计算 商贷||公积金
    public static CalculatorBean calcLoanOrFund(int refund_type, int loan_type, int mortgage_month, int loan_totalprice_type, double single_price, double area, int mortgage_ratio, double loan_interest, double fund_interest, double total_price) {
        return coreCalculate(refund_type, loan_type, mortgage_month, loan_totalprice_type, single_price, area, mortgage_ratio/10.0, loan_interest, fund_interest, total_price, 0, 0);
    }


    /**
     * @param refund_type          还款方式（AVERAGE_PRINCIPAL、AVERAGE_CAPITAL）
     * @param loan_type            贷款类别（商贷、公积金、组合贷）
     * @param mortgage_month       贷款月数
     * @param loan_totalprice_type 计算方式（TOTAL_LOANS、UNIT_PRICE_AND_AREA）
     *                             <p>
     *                             // 根据根据单价和面积
     * @param single_price         单价
     * @param area                 面积
     * @param mortgage_ratio       按揭成数
     * @param loan_interest        商贷利率
     * @param fund_interest        公积金贷款利率
     * @param total_price          房款总额
     *                             <p>
     *                             // 组合贷
     * @param shangyexing          商业性
     * @param gongjijin            公积金
     */
    private static CalculatorBean coreCalculate(int refund_type, int loan_type, int mortgage_month, int loan_totalprice_type, double single_price, double area, double mortgage_ratio, double loan_interest, double fund_interest, double total_price, double shangyexing, double gongjijin) {
        CalculatorBean calculatorBean = new CalculatorBean();

        double interest_rate = 0;   //利率(商贷利率/公积金贷款利率)
        double loan_amount = 0;     //贷款总额
        double down_payment = 0;    //首期付款

        // 商贷 || 公积金
        if (loan_type == LOAN_TYPE.LOAN || loan_type == LOAN_TYPE.FUND) {
            //计算利率
            interest_rate = loan_type == LOAN_TYPE.LOAN ? ArithUtil.div(loan_interest, 100) : ArithUtil.div(fund_interest, 100);

            // 根据单价和面积
            if (loan_totalprice_type == CALCULATION_TYPE.UNIT_PRICE_AND_AREA) {
                total_price = ArithUtil.mul(single_price, area);// 单　价 * 面　积
                loan_amount = ArithUtil.mul(total_price, mortgage_ratio);// 总价 * 按揭成数
                down_payment = ArithUtil.sub(total_price, loan_amount);// 总价 - 贷款额
            }

            // 根据贷款总额
            else if (loan_totalprice_type == CALCULATION_TYPE.TOTAL_LOANS) {
                loan_amount = total_price;
                down_payment = 0;
            }
            calculatorBean = getRightInfoNotComb(loan_amount, mortgage_month, interest_rate, refund_type);
        }
        // 组合贷
        else if (loan_type == LOAN_TYPE.PORTFOLIO) {
            loan_amount = ArithUtil.add(shangyexing, gongjijin);
            calculatorBean = getRightInfoComb(shangyexing, loan_interest, gongjijin, fund_interest, mortgage_month, refund_type);
        }

        //填充其他数据
        calculatorBean.total_price = total_price;
        calculatorBean.loan_amount = loan_amount;
        calculatorBean.down_payment = down_payment;
        calculatorBean.mortgage_month = mortgage_month;

        return calculatorBean;
    }

    //商贷&公积金
    private static CalculatorBean getRightInfoNotComb(double loan_amount, int mortgage_month, double interest, int loan_type) {
        double total_price_huankuan = 0;    //总还款
        double price_per_month = 0;         // 每月利息
        double total_interest = 0;          // 总利率
        Double[] month_payment_list = new Double[mortgage_month];   //每月支付金额

        //商贷
        if (loan_type == LOAN_TYPE.LOAN) {
            //利息月平均增加资本
            price_per_month = averageCapitalPlusInterestMonth(loan_amount, mortgage_month, interest);
            //全部利息
            total_price_huankuan = ArithUtil.mul(price_per_month, mortgage_month);
            total_interest = ArithUtil.sub(total_price_huankuan, loan_amount);// 全部利息 - 贷款额
        }
        //公积金
        else if (loan_type == LOAN_TYPE.FUND) {
            for (int i = 0; i < mortgage_month; i++) {
                // 月平均资本
                double c = averageCapitalMonth(loan_amount, mortgage_month, i, interest);
                // 全部利息
                total_price_huankuan = ArithUtil.add(total_price_huankuan, c);
                month_payment_list[i] = c;
            }
            price_per_month = ArithUtil.div(total_price_huankuan, mortgage_month);
            total_interest = ArithUtil.sub(total_price_huankuan, loan_amount);
        }
        CalculatorBean calculatorBean = new CalculatorBean();
        calculatorBean.totalPriceHuankuan = total_price_huankuan;
        calculatorBean.pricePerMonth = price_per_month;
        calculatorBean.totalInterest = total_interest;
        calculatorBean.monthPaymentList = month_payment_list;
        calculatorBean.loan_amount = loan_amount;
        calculatorBean.total_price = 0.0;
        calculatorBean.down_payment = 0.0;
        calculatorBean.mortgage_month = mortgage_month;

        return calculatorBean;
    }

    private static double averageCapitalPlusInterestMonth(double loan_amount, double mortgage_month, double interest) {
        double tmp = ArithUtil.div(interest, 12);
        //TODO:可能精度丢失
        return (double) (loan_amount * Math.pow(1 + tmp, mortgage_month) * tmp / (Math.pow(1 + tmp, mortgage_month) - 1));
    }

    private static double averageCapitalMonth(double loan_amount, double mortgage_month, double month, double interest) {
        double i = ArithUtil.div(interest, 12);
        double s = ArithUtil.div(loan_amount, mortgage_month);
        return ArithUtil.add(s, ArithUtil.mul(ArithUtil.sub(loan_amount, ArithUtil.mul(month, s)), i));
    }


    //组合贷
    private static CalculatorBean getRightInfoComb(double shangyexing, double loan_interest, double gongjijin, double fund_interest, int mortgage_month, int loan_type) {
        CalculatorBean u = new CalculatorBean();
        CalculatorBean f = getRightInfoNotComb(shangyexing, mortgage_month, loan_interest/100.0, loan_type);
        CalculatorBean l = getRightInfoNotComb(gongjijin, mortgage_month, fund_interest/100.0, loan_type);


        u.total_price = f.total_price + l.total_price;
        u.loan_amount = f.loan_amount + l.loan_amount;
        u.totalPriceHuankuan = f.totalPriceHuankuan + l.totalPriceHuankuan;
        u.totalInterest = f.totalInterest + l.totalInterest;
        u.down_payment = f.down_payment + l.down_payment;
        u.mortgage_month = f.mortgage_month + l.mortgage_month;
        u.pricePerMonth = f.pricePerMonth + l.pricePerMonth;
        if (f.monthPaymentList!=null && f.monthPaymentList[0]!=null) {
            u.monthPaymentList = new Double[f.monthPaymentList.length];
            for (int i = 0; i < f.monthPaymentList.length; i++) {
                u.monthPaymentList[i] = f.monthPaymentList[i] + l.monthPaymentList[i];
            }
        }
        u.type = loan_type;
        return u;
    }
}
