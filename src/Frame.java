import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

/**
 * @author dzzhyk
 */
public class Frame extends JFrame {


    private double loan_rate = 0.0;
    private double fund_rate = 0.0;
    private Vector<String> names = new Vector<>(Arrays.asList("计算方式","单价","面积","按揭成数","房款总额","还款方式","商贷金额","公积金金额","贷款总额","还款月数","还款总额","商贷利率","公积金利率","利息总额","每月还款金额"));

    public Frame() {
        initComponents();
        this.setVisible(true);
    }

    private void textField1FocusGained(FocusEvent e) {
        if ("请输入贷款总额（单位万）".equals(textField1.getText())){
            textField1.setText("");
        }
    }

    private void textField3FocusGained(FocusEvent e) {
        if ("输入商业贷款总额（单位万）".equals(textField3.getText())){
            textField3.setText("");
        }
    }

    private void textField4FocusGained(FocusEvent e) {
        if ("输入公积金贷款总额（单位万）".equals(textField4.getText())){
            textField4.setText("");
        }
    }

    private void textField1FocusLost(FocusEvent e) {
        if ("".equals(textField1.getText())){
            textField1.setText("请输入贷款总额（单位万）");
        }
    }

    private void textField3FocusLost(FocusEvent e) {
        if ("".equals(textField3.getText())){
            textField3.setText("输入商业贷款总额（单位万）");
        }
    }

    private void textField4FocusLost(FocusEvent e) {
        if ("".equals(textField4.getText())){
            textField4.setText("输入公积金贷款总额（单位万）");
        }
    }

    private void button3ActionPerformed(ActionEvent e) {
        if (!frame1.isShowing()){
            frame1.setVisible(true);
            // 获取所有本地记录
            List<CalculatorBean> beans = SaveUtils.getBeans();
            // 创建tableModel
            DefaultTableModel model = new DefaultTableModel();
             // 数据行向量，使用它的add()添加元素，比如整数、String、Object等，有几行就new几个行向量
            Vector<Vector<String>> data = new Vector<>(); // 数据行向量集，因为列表不止一行，往里e69da5e887aae79fa5e9819331333337393634面添加数据行向量，添加方法add(row)


            // 创建列表
            for (CalculatorBean bean : beans) {
                Vector<String> row = new Vector<>();
                row.add(bean.areaOrTotal);
                row.add(bean.price==null?"-":String.format("%.2f", bean.price));
                row.add(bean.area==null?"-":String.format("%.2f", bean.area));
                row.add(bean.mortgage_ratio==null?"-":bean.mortgage_ratio.toString());
                row.add(bean.total_price==null?"0":String.format("%.2f", bean.total_price));
                row.add(bean.type==1?"等额本息":"等额本金");
                row.add(String.format("%.2f", bean.loan));
                row.add(String.format("%.2f", bean.fund));
                row.add(String.format("%.2f", bean.loan_amount));
                row.add(bean.mortgage_month.toString());
                row.add(String.format("%.2f", bean.totalPriceHuankuan));
                row.add(String.format("%.3f%%", bean.loan_ratio));
                row.add(String.format("%.3f%%", bean.fund_ratio));
                row.add(String.format("%.2f", bean.totalInterest));
                row.add(String.format("%.2f", bean.pricePerMonth));
                data.add(row);
            }

            // 设置数据
            model.setDataVector(data, names);
            table1.setModel(model);
        }
    }


    private void button2ActionPerformed(ActionEvent e) {
        // 结果bean
        CalculatorBean bean = new CalculatorBean();
        // 获取商贷or公积金or混合贷 - 默认商贷
        int loan_type;
        if (radioButton3.isSelected() && radioButton4.isSelected()){
            loan_type = 3;
        }else if (radioButton3.isSelected()){
            loan_type = 1;
        }else {
            loan_type = 2;
        }
        // 还款方式
        int refund_type;
        // 判断还款方式
        if (radioButton1.isSelected()){
            refund_type = 1;
        }else{
            refund_type = 2;
        }
        // 还款年限
        Integer years = 0;
        // 单价
        Double price = 0.0;
        // 面积
        Double area  = 0.0;
        // 贷款总额
        Double amount = 0.0;
        // 按揭成数
        int ratio = 0;

        if (loan_type==3){
            // 如果是混合贷
            // 获取商业贷、公积金数额
            Double loan;
            Double fund;
            if (textField3.isEditValid() &&
                textField4.isEditValid()){
                loan = Double.valueOf(textField3.getValue().toString()) *10000;
                fund = Double.valueOf(textField4.getValue().toString()) *10000;
                // 获取贷款年限
                years = comboBox3.getSelectedIndex();
                if (years!=0){
                    if (years == 21){
                        years = 25;
                    }else if (years==22){
                        years = 30;
                    }
                    // 计算组合贷
                    bean = CalculatorUtils.calcPortfolio(
                            refund_type,
                            years * 12,
                            loan_rate,
                            fund_rate,
                            loan,
                            fund);
                    // 完善设置bean信息
                    bean.areaOrTotal = "混合贷款";
                    bean.price = price;
                    bean.area = area;
                    bean.total_price = price * area;
                    bean.mortgage_ratio = ratio;
                    bean.type = refund_type;
                    bean.loan = loan;
                    bean.fund = fund;
                    bean.total_price = loan + fund;
                    bean.loan_ratio = loan_rate;
                    bean.fund_ratio = fund_rate;
                }
            }
        }else {
            // 如果是商贷、公积金
            // 获取贷款计算方式
            boolean areaOrTotal = radioButton5.isSelected();     // 默认按照面积、单价计算

            if (areaOrTotal){
                // 按照面积、单价计算
                // 检查填写面积单价是否正确
                if (textField2.isEditValid() &&
                    textField5.isEditValid()){
                    // 获取单价面积
                    price = Double.valueOf(textField2.getValue().toString());
                    area = Double.valueOf(textField5.getValue().toString());
                    // 检查按揭成数
                    int index = comboBox4.getSelectedIndex();
                    if (index!=0){
                        // 获取按揭成数
                        ratio = index+1;

                        years = comboBox3.getSelectedIndex();
                        if (years!=0){
                            if (years == 21){
                                years = 25;
                            }else if (years==22){
                                years = 30;
                            }
                            // 计算贷款 - 按照面积、单价计算
                            bean = CalculatorUtils.calcLoanOrFund(
                              refund_type,
                              loan_type,
                              years * 12,
                                    2,
                                    price,
                                    area,
                                    ratio,
                                    loan_rate,
                                    fund_rate,
                                    area * price
                            );
                            // 完善设置bean信息
                            bean.areaOrTotal = "面积单价";
                            bean.price = price;
                            bean.area = area;
                            bean.total_price = price * area;
                            bean.mortgage_ratio = ratio;
                            bean.type = refund_type;
                            if (loan_type==1){
                                bean.loan = bean.total_price;
                                bean.fund = 0.0;
                            }else {
                                bean.loan = 0.0;
                                bean.fund = bean.total_price;
                            }
                            bean.loan_ratio = loan_rate;
                            bean.fund_ratio = fund_rate;
                        }
                    }
                }
            }else {
                // 按照贷款总额计算
                // 检查填写贷款总额是否正确
                if (textField1.isEditValid()){
                    // 获取贷款总额
                    amount = Double.valueOf(textField1.getValue().toString()) *10000;
                    // 获取贷款年限
                    years = comboBox3.getSelectedIndex();
                    if (years!=0){
                        if (years == 21){
                            years = 25;
                        }else if (years==22){
                            years = 30;
                        }
                        // 计算贷款 - 按照贷款总额计算
                        bean = CalculatorUtils.calcLoanOrFund(
                                refund_type,
                                loan_type,
                                years * 12,
                                1,
                                0,
                                0,
                                0,
                                loan_rate,
                                fund_rate,
                                amount);

                        // 完善设置bean信息
                        bean.areaOrTotal = "贷款总额";
                        bean.price = price;
                        bean.area = area;
                        bean.total_price = null;
                        bean.mortgage_ratio = ratio;
                        bean.type = refund_type;
                        if (loan_type==1){
                            bean.loan = amount;
                            bean.fund = 0.0;
                        }else {
                            bean.loan = 0.0;
                            bean.fund = amount;
                        }
                        bean.loan_ratio = loan_rate;
                        bean.fund_ratio = fund_rate;
                    }
                }
            }
        }
        // 展示信息
        textArea7.setText(bean.total_price==null?"略":String.format("%.2f", bean.total_price));
        textArea2.setText(bean.loan_amount==null?"0":String.format("%.2f", bean.loan_amount));
        textArea3.setText(bean.totalPriceHuankuan==null?"0":String.format("%.2f", bean.totalPriceHuankuan));
        textArea4.setText(bean.totalInterest==null?"0":String.format("%.2f", bean.totalInterest));
        textArea5.setText(bean.down_payment!=null&&bean.down_payment!=0?String.format("%.2f", bean.down_payment):"0");
        textArea6.setText(bean.mortgage_month!=null&&bean.mortgage_month!=0?bean.mortgage_month.toString():"0");

        // 特殊信息根据还款方式展示不同
        if (bean.type==1) {
            // 等额本息
            textArea8.setText(bean.pricePerMonth!=null&&bean.pricePerMonth!=0?String.format("%.2f", bean.pricePerMonth):"0");
        }else {
            // 等额本金
            if (bean.monthPaymentList!=null){
                DefaultListModel dlm = new DefaultListModel();
//                list1.setModel(dlm);
                for (int i = 0; i < bean.monthPaymentList.length; i++) {
                    dlm.add(i, (i+1)+"月, "+String.format("%.2f", bean.monthPaymentList[i])+"(元)");

                }
                list1.setModel(dlm);
            }
        }
        // 存入记录表格
        SaveUtils.saveBean(bean);
    }
    private void radioButton3ActionPerformed(ActionEvent e) {
        if (!radioButton4.isSelected() && !radioButton3.isSelected()){
            radioButton3.setSelected(true);
        }else {
            if (radioButton3.isSelected() && radioButton4.isSelected()){
                // 组合贷
                label1.setVisible(false);
                label3.setVisible(false);
                label19.setVisible(false);
                label20.setVisible(false);
                label21.setVisible(false);
                label22.setVisible(false);
                label23.setVisible(false);
                label37.setVisible(false);
                textField1.setVisible(false);
                textField2.setVisible(false);
                textField5.setVisible(false);
                radioButton5.setVisible(false);
                radioButton6.setVisible(false);
                comboBox4.setVisible(false);

                label7.setEnabled(true);
                label8.setEnabled(true);
                label38.setEnabled(true);
                label39.setEnabled(true);
                textField3.setEnabled(true);
                textField4.setEnabled(true);

                label40.setVisible(true);
            }else{
                label40.setVisible(false);
                label37.setVisible(true);
                radioButton5.setVisible(true);
                radioButton6.setVisible(true);
                textField3.setEnabled(false);
                textField4.setEnabled(false);
                label7.setEnabled(false);
                label8.setEnabled(false);
                label38.setEnabled(false);
                label39.setEnabled(false);
                if (radioButton5.isSelected()){
                    radioButton6.setSelected(false);
                    label1.setVisible(false);
                    textField1.setVisible(false);
                    label3.setVisible(false);

                    label19.setVisible(true);
                    label20.setVisible(true);
                    label21.setVisible(true);
                    label22.setVisible(true);
                    label23.setVisible(true);
                    textField2.setVisible(true);
                    textField5.setVisible(true);
                    comboBox4.setVisible(true);
                }else {
                    radioButton5.setSelected(false);
                    label1.setVisible(true);
                    textField1.setVisible(true);
                    label3.setVisible(true);

                    label19.setVisible(false);
                    label20.setVisible(false);
                    label21.setVisible(false);
                    label22.setVisible(false);
                    label23.setVisible(false);
                    textField2.setVisible(false);
                    textField5.setVisible(false);
                    comboBox4.setVisible(false);
                }
            }
        }

        comboBox3ActionPerformed(e);
    }

    private void radioButton4ActionPerformed(ActionEvent e) {
        if (!radioButton3.isSelected() && !radioButton4.isSelected()){
            radioButton4.setSelected(true);
        }else {
            if (radioButton3.isSelected() && radioButton4.isSelected()){
                // 组合贷
                label1.setVisible(false);
                label3.setVisible(false);
                label19.setVisible(false);
                label20.setVisible(false);
                label21.setVisible(false);
                label22.setVisible(false);
                label23.setVisible(false);
                label37.setVisible(false);
                textField1.setVisible(false);
                textField2.setVisible(false);
                textField5.setVisible(false);
                radioButton5.setVisible(false);
                radioButton6.setVisible(false);
                comboBox4.setVisible(false);
                label7.setEnabled(true);
                label8.setEnabled(true);
                label38.setEnabled(true);
                label39.setEnabled(true);
                textField3.setEnabled(true);
                textField4.setEnabled(true);
                label40.setVisible(true);

            }else{
                label40.setVisible(false);
                label37.setVisible(true);
                radioButton5.setVisible(true);
                radioButton6.setVisible(true);
                textField3.setEnabled(false);
                textField4.setEnabled(false);
                label7.setEnabled(false);
                label8.setEnabled(false);
                label38.setEnabled(false);
                label39.setEnabled(false);
                if (radioButton5.isSelected()){
                    radioButton6.setSelected(false);
                    label1.setVisible(false);
                    textField1.setVisible(false);
                    label3.setVisible(false);

                    label19.setVisible(true);
                    label20.setVisible(true);
                    label21.setVisible(true);
                    label22.setVisible(true);
                    label23.setVisible(true);
                    textField2.setVisible(true);
                    textField5.setVisible(true);
                    comboBox4.setVisible(true);
                }else {
                    radioButton5.setSelected(false);
                    label1.setVisible(true);
                    textField1.setVisible(true);
                    label3.setVisible(true);

                    label19.setVisible(false);
                    label20.setVisible(false);
                    label21.setVisible(false);
                    label22.setVisible(false);
                    label23.setVisible(false);
                    textField2.setVisible(false);
                    textField5.setVisible(false);
                    comboBox4.setVisible(false);
                }
            }
        }

        comboBox3ActionPerformed(e);
    }

    private void radioButton1ActionPerformed(ActionEvent e) {
        if (!radioButton1.isSelected()){
            radioButton1.setSelected(true);
        }else {
            radioButton2.setSelected(false);
            DefaultListModel dlm = new DefaultListModel();
            list1.setModel(dlm);
            list1.setEnabled(false);
            label29.setEnabled(false);
            label42.setEnabled(true);
            label43.setEnabled(true);
            textArea8.setEnabled(true);
        }
    }

    private void radioButton2ActionPerformed(ActionEvent e) {
        if (!radioButton2.isSelected()){
            radioButton2.setSelected(true);
        }else {
            radioButton1.setSelected(false);
            list1.setEnabled(true);
            label29.setEnabled(true);
            label42.setEnabled(false);
            label43.setEnabled(false);

            textArea8.setText("");
            textArea8.setEnabled(false);
        }
    }

    private void radioButton3StateChanged(ChangeEvent e) {
        // TODO add your code here
    }

    private void radioButton6ActionPerformed(ActionEvent e) {
        if (!radioButton6.isSelected()){
            radioButton6.setSelected(true);
        }else {
            radioButton5.setSelected(false);
            label1.setVisible(true);
            textField1.setVisible(true);
            label3.setVisible(true);

            label19.setVisible(false);
            label20.setVisible(false);
            label21.setVisible(false);
            label22.setVisible(false);
            label23.setVisible(false);
            textField2.setVisible(false);
            textField5.setVisible(false);
            comboBox4.setVisible(false);
        }
    }

    private void radioButton5ActionPerformed(ActionEvent e) {
        if (!radioButton5.isSelected()){
            radioButton5.setSelected(true);
        }else {
            radioButton6.setSelected(false);
            label1.setVisible(false);
            textField1.setVisible(false);
            label3.setVisible(false);

            label19.setVisible(true);
            label20.setVisible(true);
            label21.setVisible(true);
            label22.setVisible(true);
            label23.setVisible(true);
            textField2.setVisible(true);
            textField5.setVisible(true);
            comboBox4.setVisible(true);
        }
    }

    private void comboBox3ActionPerformed(ActionEvent e) {
        // 根据贷款方式、贷款年份 显示不同利率
        int years = comboBox3.getSelectedIndex();
        if (years!=0){
            if (years==1){
                loan_rate = 4.35;
                fund_rate = 2.75;
            }else if (years > 1 && years <= 5){
                loan_rate = 4.75;
                fund_rate = 2.75;
            }else{
                loan_rate = 4.90;
                fund_rate = 3.25;
            }
        }
        if (radioButton3.isSelected() && radioButton4.isSelected()){
            // 组合贷
            label18.setText(String.format("商贷：%.3f%% \t公积金：%.3f%%", loan_rate, fund_rate));
        }else if (radioButton3.isSelected()){
            // 商贷
            label18.setText(String.format("商贷：%.3f%%", loan_rate));
        }else {
            // 公积金
            label18.setText(String.format("公积金：%.3f%%", fund_rate));
        }
    }

    private void comboBox1ActionPerformed(ActionEvent e) {
        // TODO add your code here
    }

    private void initComponents() {
        label1 = new JLabel();
        textField1 = new JFormattedTextField(NumberFormat.getNumberInstance());
        label3 = new JLabel();
        label6 = new JLabel();
        radioButton1 = new JRadioButton();
        radioButton2 = new JRadioButton();
        radioButton3 = new JRadioButton();
        radioButton4 = new JRadioButton();
        textField3 = new JFormattedTextField(NumberFormat.getNumberInstance());
        textField4 = new JFormattedTextField(NumberFormat.getNumberInstance());
        label7 = new JLabel();
        label8 = new JLabel();
        label9 = new JLabel();
        label10 = new JLabel();
        button2 = new JButton();
        label11 = new JLabel();
        vSpacer1 = new JPanel(null);
        button3 = new JButton();
        label18 = new JLabel();
        radioButton5 = new JRadioButton();
        radioButton6 = new JRadioButton();
        comboBox3 = new JComboBox<>();
        label19 = new JLabel();
        label20 = new JLabel();
        textField2 = new JFormattedTextField(NumberFormat.getNumberInstance());
        label21 = new JLabel();
        textField5 = new JFormattedTextField(NumberFormat.getNumberInstance());
        label22 = new JLabel();
        label23 = new JLabel();
        comboBox4 = new JComboBox<>();
        label24 = new JLabel();
        label25 = new JLabel();
        label26 = new JLabel();
        label27 = new JLabel();
        label28 = new JLabel();
        label29 = new JLabel();
        scrollPane2 = new JScrollPane();
        list1 = new JList();
        hSpacer2 = new JPanel(null);
        textArea2 = new JTextArea();
        textArea3 = new JTextArea();
        textArea4 = new JTextArea();
        textArea5 = new JTextArea();
        textArea6 = new JTextArea();
        textArea7 = new JTextArea();
        label30 = new JLabel();
        label31 = new JLabel();
        label32 = new JLabel();
        label33 = new JLabel();
        label34 = new JLabel();
        label35 = new JLabel();
        label36 = new JLabel();
        label37 = new JLabel();
        label38 = new JLabel();
        label39 = new JLabel();
        label40 = new JLabel();
        label41 = new JLabel();
        label42 = new JLabel();
        textArea8 = new JTextArea();
        label43 = new JLabel();
        frame1 = new JFrame();
        scrollPane1 = new JScrollPane();
        table1 = new JTable();
        hSpacer1 = new JPanel(null);
        vSpacer2 = new JPanel(null);

        setTitle("贷款计算器 \t by: CC_且听风吟");
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Container contentPane = getContentPane();
        contentPane.setLayout(null);

        label1.setText("\u8d37\u6b3e\u603b\u989d\uff1a");
        label1.setVisible(false);
        contentPane.add(label1);
        label1.setBounds(20, 120, 80, 35);

        textField1.setText("\u8bf7\u8f93\u5165\u8d37\u6b3e\u603b\u989d\uff08\u5355\u4f4d\u4e07\uff09");
        textField1.setHorizontalAlignment(SwingConstants.RIGHT);
        textField1.setVisible(false);
        textField1.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                textField1FocusGained(e);
            }
            @Override
            public void focusLost(FocusEvent e) {
                textField1FocusLost(e);
            }
        });
        contentPane.add(textField1);
        textField1.setBounds(95, 120, 235, 35);

        label3.setText("\u4e07");
        label3.setVisible(false);
        contentPane.add(label3);
        label3.setBounds(335, 125, 30, 25);

        label6.setText("\u8fd8\u6b3e\u65b9\u5f0f\uff1a");
        contentPane.add(label6);
        label6.setBounds(30, 200, 80, 30);

        radioButton1.setText("\u7b49\u989d\u672c\u606f");
        radioButton1.setSelected(true);
        radioButton1.addActionListener(e -> {
			radioButton1ActionPerformed(e);
		});
        contentPane.add(radioButton1);
        radioButton1.setBounds(new Rectangle(new Point(120, 205), radioButton1.getPreferredSize()));

        radioButton2.setText("\u7b49\u989d\u672c\u91d1");
        radioButton2.addActionListener(e -> {
			radioButton2ActionPerformed(e);
		});
        contentPane.add(radioButton2);
        radioButton2.setBounds(230, 205, 90, 22);

        radioButton3.setText("\u5546\u8d37");
        radioButton3.setSelected(true);
        radioButton3.addChangeListener(e -> radioButton3StateChanged(e));
        radioButton3.addActionListener(e -> {
			radioButton3ActionPerformed(e);
		});
        contentPane.add(radioButton3);
        radioButton3.setBounds(105, 10, 70, 40);

        radioButton4.setText("\u516c\u79ef\u91d1");
        radioButton4.addActionListener(e -> {
			radioButton4ActionPerformed(e);
		});
        contentPane.add(radioButton4);
        radioButton4.setBounds(180, 10, 95, 40);

        textField3.setText("\u8f93\u5165\u5546\u4e1a\u8d37\u6b3e\u603b\u989d\uff08\u5355\u4f4d\u4e07\uff09");
        textField3.setHorizontalAlignment(SwingConstants.RIGHT);
        textField3.setEnabled(false);
        textField3.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                textField3FocusGained(e);
            }
            @Override
            public void focusLost(FocusEvent e) {
                textField3FocusLost(e);
            }
        });
        contentPane.add(textField3);
        textField3.setBounds(110, 235, 220, 35);

        textField4.setHorizontalAlignment(SwingConstants.RIGHT);
        textField4.setText("\u8f93\u5165\u516c\u79ef\u91d1\u8d37\u6b3e\u603b\u989d\uff08\u5355\u4f4d\u4e07\uff09");
        textField4.setEnabled(false);
        textField4.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                textField4FocusGained(e);
            }
            @Override
            public void focusLost(FocusEvent e) {
                textField4FocusLost(e);
            }
        });
        contentPane.add(textField4);
        textField4.setBounds(110, 275, 220, 35);

        label7.setText("\u4e07");
        label7.setEnabled(false);
        contentPane.add(label7);
        label7.setBounds(335, 240, 23, 20);

        label8.setText("\u4e07");
        label8.setEnabled(false);
        contentPane.add(label8);
        label8.setBounds(335, 280, 23, 20);

        label9.setText("\u8d37\u6b3e\u5e74\u9650\uff1a");
        contentPane.add(label9);
        label9.setBounds(15, 320, 85, 40);

        label10.setText("\u57fa\u51c6\u5229\u7387\uff1a");
        contentPane.add(label10);
        label10.setBounds(15, 365, 85, 40);

        button2.setText("\u8ba1\u7b97\u8fd8\u6b3e\u660e\u7ec6");
        button2.setForeground(new Color(255, 51, 51));
        button2.addActionListener(e -> {
			button2ActionPerformed(e);
		});
        contentPane.add(button2);
        button2.setBounds(10, 410, 345, 35);

        label11.setText("\u623f\u6b3e\u603b\u989d\uff1a");
        contentPane.add(label11);
        label11.setBounds(385, 20, 65, 25);
        contentPane.add(vSpacer1);
        vSpacer1.setBounds(320, 450, 30, 20);

        button3.setText("\u8ba1\u7b97\u5386\u53f2");
        button3.addActionListener(e -> {
			button3ActionPerformed(e);
		});
        contentPane.add(button3);
        button3.setBounds(380, 380, 75, 70);

        label18.setHorizontalAlignment(SwingConstants.CENTER);
        label18.setText("\u5546\u8d37\uff1a0.000%");
        contentPane.add(label18);
        label18.setBounds(100, 365, 230, 35);

        radioButton5.setText("\u6839\u636e\u9762\u79ef\u3001\u5355\u4ef7\u8ba1\u7b97");
        radioButton5.setSelected(true);
        radioButton5.addActionListener(e -> {
			radioButton5ActionPerformed(e);
		});
        contentPane.add(radioButton5);
        radioButton5.setBounds(75, 50, 155, 35);

        radioButton6.setText("\u6839\u636e\u8d37\u6b3e\u603b\u989d\u8ba1\u7b97");
        radioButton6.addActionListener(e -> {
			radioButton6ActionPerformed(e);
		});
        contentPane.add(radioButton6);
        radioButton6.setBounds(230, 50, 155, 35);

        comboBox3.setModel(new DefaultComboBoxModel<>(new String[] {
            "--\u8bf7\u9009\u62e9--",
            "1\u5e74",
            "2\u5e74",
            "3\u5e74",
            "4\u5e74",
            "5\u5e74",
            "6\u5e74",
            "7\u5e74",
            "8\u5e74",
            "9\u5e74",
            "10\u5e74",
            "11\u5e74",
            "12\u5e74",
            "13\u5e74",
            "14\u5e74",
            "15\u5e74",
            "16\u5e74",
            "17\u5e74",
            "18\u5e74",
            "19\u5e74",
            "20\u5e74",
            "25\u5e74",
            "30\u5e74"
        }));
        comboBox3.setMaximumRowCount(23);
        comboBox3.addActionListener(e -> {
			comboBox1ActionPerformed(e);
			comboBox3ActionPerformed(e);
		});
        contentPane.add(comboBox3);
        comboBox3.setBounds(90, 325, 140, 35);

        label19.setText("\u5355\u4ef7\uff1a");
        contentPane.add(label19);
        label19.setBounds(15, 100, 45, 25);

        label20.setText("\u9762\u79ef\uff1a");
        contentPane.add(label20);
        label20.setBounds(205, 100, 45, 25);

        textField2.setHorizontalAlignment(SwingConstants.RIGHT);
        contentPane.add(textField2);
        textField2.setBounds(50, 95, 75, 35);

        label21.setText("\u5143/\u5e73\u65b9\u7c73");
        contentPane.add(label21);
        label21.setBounds(125, 100, 60, 25);

        textField5.setHorizontalAlignment(SwingConstants.RIGHT);
        contentPane.add(textField5);
        textField5.setBounds(240, 95, 75, 35);

        label22.setText("\u5e73\u65b9\u7c73");
        contentPane.add(label22);
        label22.setBounds(315, 100, 50, 25);

        label23.setText("\u6309\u63ed\u6210\u6570\uff1a");
        contentPane.add(label23);
        label23.setBounds(15, 145, 75, 30);

        comboBox4.setModel(new DefaultComboBoxModel<>(new String[] {
            "--\u8bf7\u9009\u62e9--",
            "2\u6210",
            "3\u6210",
            "4\u6210",
            "5\u6210",
            "6\u6210",
            "7\u6210",
            "8\u6210",
            "9\u6210"
        }));
        comboBox4.setMaximumRowCount(9);
        comboBox4.addActionListener(e -> comboBox1ActionPerformed(e));
        contentPane.add(comboBox4);
        comboBox4.setBounds(90, 145, 245, 35);

        label24.setText("\u8d37\u6b3e\u603b\u989d\uff1a");
        contentPane.add(label24);
        label24.setBounds(385, 55, 65, 25);

        label25.setText("\u8fd8\u6b3e\u603b\u989d\uff1a");
        contentPane.add(label25);
        label25.setBounds(385, 90, 65, 25);

        label26.setText("\u652f\u4ed8\u5229\u606f\u6b3e\uff1a");
        contentPane.add(label26);
        label26.setBounds(385, 125, 80, 25);

        label27.setText("\u9996\u671f\u4ed8\u6b3e\uff1a");
        contentPane.add(label27);
        label27.setBounds(385, 160, 65, 25);

        label28.setText("\u8d37\u6b3e\u6708\u6570\uff1a");
        contentPane.add(label28);
        label28.setBounds(385, 195, 65, 25);

        label29.setText("\u6708\u5747\u91d1\u989d\uff1a ");
        label29.setEnabled(false);
        contentPane.add(label29);
        label29.setBounds(385, 285, 75, 25);

        {

            list1.setEnabled(false);
            list1.setVisibleRowCount(10);
            scrollPane2.setViewportView(list1);
        }
        contentPane.add(scrollPane2);
        scrollPane2.setBounds(475, 285, 180, 175);
        contentPane.add(hSpacer2);
        hSpacer2.setBounds(655, 400, 15, 15);

        textArea2.setEditable(false);
        contentPane.add(textArea2);
        textArea2.setBounds(470, 60, 115, 21);

        textArea3.setEditable(false);
        contentPane.add(textArea3);
        textArea3.setBounds(470, 95, 115, 21);

        textArea4.setEditable(false);
        contentPane.add(textArea4);
        textArea4.setBounds(470, 130, 115, 21);

        textArea5.setEditable(false);
        contentPane.add(textArea5);
        textArea5.setBounds(470, 165, 115, 21);

        textArea6.setEditable(false);
        contentPane.add(textArea6);
        textArea6.setBounds(470, 200, 115, 21);

        textArea7.setEditable(false);
        contentPane.add(textArea7);
        textArea7.setBounds(470, 25, 115, 21);

        label30.setText("\u5143");
        contentPane.add(label30);
        label30.setBounds(600, 25, 20, 20);

        label31.setText("\u5143");
        contentPane.add(label31);
        label31.setBounds(600, 60, 20, 20);

        label32.setText("\u5143");
        contentPane.add(label32);
        label32.setBounds(600, 95, 20, 20);

        label33.setText("\u5143");
        contentPane.add(label33);
        label33.setBounds(600, 130, 20, 20);

        label34.setText("\u5143");
        contentPane.add(label34);
        label34.setBounds(600, 165, 20, 20);

        label35.setText("\u6708");
        contentPane.add(label35);
        label35.setBounds(600, 200, 20, 20);

        label36.setText("\u8d37\u6b3e\u7c7b\u522b\uff1a");
        contentPane.add(label36);
        label36.setBounds(15, 20, 70, 25);

        label37.setText("\u8ba1\u7b97\u65b9\u5f0f\uff1a");
        contentPane.add(label37);
        label37.setBounds(15, 55, 70, 25);

        label38.setText("\u5546\u4e1a\u8d37\uff1a");
        label38.setEnabled(false);
        contentPane.add(label38);
        label38.setBounds(30, 240, 55, 25);

        label39.setText("\u516c\u79ef\u91d1\uff1a");
        label39.setEnabled(false);
        contentPane.add(label39);
        label39.setBounds(30, 280, 55, 25);

        label40.setText("\u6df7\u5408\u8d37");
        label40.setForeground(Color.red);
        label40.setVisible(false);
        contentPane.add(label40);
        label40.setBounds(275, 15, 60, 30);

        label41.setText("2019\u5e743\u67081\u65e5\u57fa\u51c6\u5229\u7387");
        contentPane.add(label41);
        label41.setBounds(235, 330, 145, 25);

        label42.setText("\u6708\u5747\u8fd8\u6b3e\uff1a ");
        contentPane.add(label42);
        label42.setBounds(385, 240, 75, 25);

        textArea8.setEditable(false);
        contentPane.add(textArea8);
        textArea8.setBounds(470, 240, 115, 21);

        label43.setText("\u5143");
        contentPane.add(label43);
        label43.setBounds(600, 240, 20, 20);

        {
            Dimension preferredSize = new Dimension();
            for(int i = 0; i < contentPane.getComponentCount(); i++) {
                Rectangle bounds = contentPane.getComponent(i).getBounds();
                preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
            }
            Insets insets = contentPane.getInsets();
            preferredSize.width += insets.right;
            preferredSize.height += insets.bottom;
            contentPane.setMinimumSize(preferredSize);
            contentPane.setPreferredSize(preferredSize);
        }
        pack();
        setLocationRelativeTo(getOwner());

        {
            frame1.setTitle("\u8ba1\u7b97\u5386\u53f2");
            frame1.setResizable(false);
            frame1.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            Container frame1ContentPane = frame1.getContentPane();
            frame1ContentPane.setLayout(null);

            {

                table1.setModel(new DefaultTableModel(
                    new Object[][] {
                        {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                    },
                    new String[] {
                        "\u8ba1\u7b97\u65b9\u5f0f", "\u5355\u4ef7", "\u9762\u79ef", "\u6309\u63ed\u6210\u6570", "\u623f\u6b3e\u603b\u989d", "\u8fd8\u6b3e\u65b9\u5f0f", "\u5546\u8d37\u91d1\u989d", "\u516c\u79ef\u91d1\u91d1\u989d", "\u8d37\u6b3e\u603b\u989d", "\u8fd8\u6b3e\u6708\u6570", "\u8fd8\u6b3e\u603b\u989d", "\u5546\u8d37\u5229\u7387", "\u516c\u79ef\u91d1\u5229\u7387", "\u5229\u606f\u603b\u989d", "\u6bcf\u6708\u8fd8\u6b3e\u91d1\u989d"
                    }
                ) {
                    Class<?>[] columnTypes = new Class<?>[] {
                        String.class, Double.class, Double.class, Integer.class, Double.class, String.class, Double.class, Double.class, Double.class, Integer.class, Double.class, Double.class, Double.class, Double.class, Double.class
                    };
                    boolean[] columnEditable = new boolean[] {
                        false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
                    };
                    @Override
                    public Class<?> getColumnClass(int columnIndex) {
                        return columnTypes[columnIndex];
                    }
                    @Override
                    public boolean isCellEditable(int rowIndex, int columnIndex) {
                        return columnEditable[columnIndex];
                    }
                });
                table1.setSurrendersFocusOnKeystroke(true);
                table1.setFont(new Font("Kefa", Font.PLAIN, 14));
                scrollPane1.setViewportView(table1);
            }
            frame1ContentPane.add(scrollPane1);
            scrollPane1.setBounds(10, 10, 1120, 520);
            frame1ContentPane.add(hSpacer1);
            hSpacer1.setBounds(1130, 360, 15, 15);
            frame1ContentPane.add(vSpacer2);
            vSpacer2.setBounds(420, 530, 15, 15);

            {
                Dimension preferredSize = new Dimension();
                for(int i = 0; i < frame1ContentPane.getComponentCount(); i++) {
                    Rectangle bounds = frame1ContentPane.getComponent(i).getBounds();
                    preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                    preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                }
                Insets insets = frame1ContentPane.getInsets();
                preferredSize.width += insets.right;
                preferredSize.height += insets.bottom;
                frame1ContentPane.setMinimumSize(preferredSize);
                frame1ContentPane.setPreferredSize(preferredSize);
            }
            frame1.pack();
            frame1.setLocationRelativeTo(frame1.getOwner());
        }
    }

    private JLabel label1;
    private JFormattedTextField textField1;
    private JLabel label3;
    private JLabel label6;
    private JRadioButton radioButton1;
    private JRadioButton radioButton2;
    private JRadioButton radioButton3;
    private JRadioButton radioButton4;
    private JFormattedTextField textField3;
    private JFormattedTextField textField4;
    private JLabel label7;
    private JLabel label8;
    private JLabel label9;
    private JLabel label10;
    private JButton button2;
    private JLabel label11;
    private JPanel vSpacer1;
    private JButton button3;
    private JLabel label18;
    private JRadioButton radioButton5;
    private JRadioButton radioButton6;
    private JComboBox<String> comboBox3;
    private JLabel label19;
    private JLabel label20;
    private JFormattedTextField textField2;
    private JLabel label21;
    private JFormattedTextField textField5;
    private JLabel label22;
    private JLabel label23;
    private JComboBox<String> comboBox4;
    private JLabel label24;
    private JLabel label25;
    private JLabel label26;
    private JLabel label27;
    private JLabel label28;
    private JLabel label29;
    private JScrollPane scrollPane2;
    private JList list1;
    private JPanel hSpacer2;
    private JTextArea textArea2;
    private JTextArea textArea3;
    private JTextArea textArea4;
    private JTextArea textArea5;
    private JTextArea textArea6;
    private JTextArea textArea7;
    private JLabel label30;
    private JLabel label31;
    private JLabel label32;
    private JLabel label33;
    private JLabel label34;
    private JLabel label35;
    private JLabel label36;
    private JLabel label37;
    private JLabel label38;
    private JLabel label39;
    private JLabel label40;
    private JLabel label41;
    private JLabel label42;
    private JTextArea textArea8;
    private JLabel label43;
    private JFrame frame1;
    private JScrollPane scrollPane1;
    private JTable table1;
    private JPanel hSpacer1;
    private JPanel vSpacer2;
}
