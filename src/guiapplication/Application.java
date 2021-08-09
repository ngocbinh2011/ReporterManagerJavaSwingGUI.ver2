/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package guiapplication;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 *
 * @author ADMIN
 */
public class Application extends javax.swing.JFrame {

    /**
     * Creates new form Application
     */
    private final int dayStartIndex = 1, dayEndIndex = 31;
    private final int monthStartIndex = 1, monthEndIndex = 12;
    private final int yearStartIndex = 1990, yearEndIndex = 2021;
    private final String regexInformationSplit = "-";
    private final String reporterFileName = "PV.txt";
    private final String postFileName = "KB.txt";
    private final String tablePriceFileName = "TINHCONG.txt";
    
    private List<Reporter> listReporter;
    private List<Post> listPost;
    private List<TablePrice> listTablePrice;
    private List<Integer> listDays = new ArrayList<>();
    private List<Integer> listMonths = new ArrayList<>();
    private List<Integer> listYears = new ArrayList<>();
       
    public Application() {
        initComponents();
        
        listDays = loadDayMonthYearForList(dayStartIndex, dayEndIndex);
        listMonths = loadDayMonthYearForList(monthStartIndex, monthEndIndex);
        listYears = loadDayMonthYearForList(yearStartIndex, yearEndIndex);
        listReporter = (List<Reporter>) loadDataToList(reporterFileName);
        Reporter.setCurrentId(listReporter.size() + Reporter.getCurrentId());
        loadItemsForComboBox(listReporter, tablePriceReporterItem);
        listPost = (List<Post>) loadDataToList(postFileName);
        Post.setCurrentId(listPost.size() + Post.getCurrentId());
        loadItemsForComboBox(listPost, tablePricePostItem);
        listTablePrice = (List<TablePrice>) loadDataToList(tablePriceFileName);
        loadDataToTabel(listReporter, reporterTable);
        loadDataToTabel(listPost, postTable);
        loadDataToTabel(listTablePrice, tablePriceTable);
        loadItemsForComboBox(listReporter, salaryReporterItem);
        loadItemsForComboBox(listDays, reporterDayCb);
        loadItemsForComboBox(listMonths, reporterMonthCb);
        loadItemsForComboBox(listYears, reporterYearCb);
        
        reporterAddBtn.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                String reporterName = reporterNameTF.getText();
                String addressString = reporterAdressTF.getText();
                Address address = new Address(addressString);
                String reporterClass = reporterClassItem.getItemAt(reporterClassItem.getSelectedIndex());
                int day = Integer.parseInt(reporterDayCb.getItemAt(reporterDayCb.getSelectedIndex()));
                int month = Integer.parseInt(reporterMonthCb.getItemAt(reporterMonthCb.getSelectedIndex()));
                int year = Integer.parseInt(reporterYearCb.getItemAt(reporterYearCb.getSelectedIndex()));
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month - 1, day);
                Date date = calendar.getTime();
                
                if(reporterName.equals("") || addressString.equals("")){
                    (new JOptionPane()).showMessageDialog(rootPane, "ko duoc de trong");
                    return;
                }
                Reporter reporter = new Reporter(reporterName, address, reporterClass, date);
                listReporter.add(reporter);
                addRowToTable(reporter, reporterTable);
            }
        });
        
        reporterSaveBtn.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                writeToFile(listReporter, reporterFileName);
                JOptionPane.showMessageDialog(rootPane, "luu thanh cong vao file " + reporterFileName);
                loadItemsForComboBox(listReporter, tablePriceReporterItem);
                loadItemsForComboBox(listReporter, salaryReporterItem);
            }          
        });
        
        postAddBtn.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                String postName = postClassTF.getText();
                String priceString = postPriceTF.getText();
                String regex = "[0-9]+";
                if(postName.isEmpty() || priceString.isEmpty() || !checkStringWithPattern(regex, priceString)){
                    JOptionPane.showMessageDialog(rootPane, "input ko dung");
                    return;
                }
                Post post = new Post(postName, Integer.parseInt(priceString));
                listPost.add(post);
                addRowToTable(post, postTable);
            }
            
        });
        
        postSaveBtn.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                writeToFile(listPost, postFileName);
                JOptionPane.showMessageDialog(rootPane, "luu thanh cong");
                loadItemsForComboBox(listPost, tablePricePostItem);
            }
        });
        
        
        tablePriceAddBtn.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                String reporterInfo = tablePriceReporterItem.getItemAt(tablePriceReporterItem.getSelectedIndex());
                String postInfo = tablePricePostItem.getItemAt(tablePricePostItem.getSelectedIndex()); 
                String amountString = tabelPriceAmountTF.getText();
                String regex = "[0-9]+";
                if(reporterInfo == null || postInfo == null || amountString == null || 
                        !checkStringWithPattern(regex, amountString)){
                    JOptionPane.showMessageDialog(rootPane, "nhap sai");
                    return;
                }
                String[] reporterElement = reporterInfo.trim().split(regexInformationSplit);
                String[] postElement = postInfo.trim().split(regexInformationSplit);
                int reporterId = Integer.parseInt(reporterElement[0]);
                int postId = Integer.parseInt(postElement[0]);
                
                Reporter reporter = (Reporter) getIObject(listReporter, reporterId);
                Post post = (Post) getIObject(listPost, postId);
                if(isSameByPostId(post, reporter)){
                    JOptionPane.showMessageDialog(rootPane, "bai viet trung nhau");
                    return;
                }
                if(countAppearByReporterId(reporter) >= 5){
                    JOptionPane.showMessageDialog(rootPane, "ko the > 5");
                    return;
                }
                TablePrice tablePrice = new TablePrice(reporter, post, Integer.valueOf(amountString));
                listTablePrice.add(tablePrice);
//                System.out.println(Arrays.toString(tablePrice.toObject()));
                addRowToTable(tablePrice, tablePriceTable);
            }
            
        });
        
        tablePriceSaveBtn.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                writeToFile(listTablePrice, tablePriceFileName);
                JOptionPane.showMessageDialog(rootPane, "luu thanh cong");
            }
            
        });
        
        tablePriceSortByNameBtn.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                Collections.sort(listTablePrice, new Comparator<TablePrice>(){
                    @Override
                    public int compare(TablePrice o1, TablePrice o2) {
                        return o1.getReporter().getName().compareTo(o2.getReporter().getName());
                    }                   
                });
                loadDataToTabel(listTablePrice, tablePriceTable);
            }
            
        });
        
        tablePriceSoryByAmountBtn.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                Collections.sort(listTablePrice, new Comparator<TablePrice>(){
                    @Override
                    public int compare(TablePrice o1, TablePrice o2) {
                        return o1.getAmount() > o2.getAmount()? -1 : 1;
                    }                   
                });
                loadDataToTabel(listTablePrice, tablePriceTable);
            }
            
        });
               
        salaryBtn.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                String reporterInfo = salaryReporterItem.getItemAt(salaryReporterItem.getSelectedIndex());
                if(reporterInfo == null){
                    JOptionPane.showMessageDialog(rootPane, "ngu hoc");
                    return;
                }
                String[] reporterArr = reporterInfo.trim().split(regexInformationSplit);
                Reporter reporter = (Reporter) getIObject(listReporter, Integer.valueOf(reporterArr[0]));
                int salary = calculateSalary(reporter);
                JOptionPane.showMessageDialog(rootPane, "Salary = " + salary);
            }
            
        });
       
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        reporterNameTF = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        reporterAdressTF = new javax.swing.JTextField();
        reporterClassItem = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        reporterAddBtn = new javax.swing.JButton();
        reporterSaveBtn = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        reporterTable = new javax.swing.JTable();
        reporterDayCb = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        reporterMonthCb = new javax.swing.JComboBox<>();
        reporterYearCb = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        postClassTF = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        postPriceTF = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        postTable = new javax.swing.JTable();
        postAddBtn = new javax.swing.JButton();
        postSaveBtn = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        tablePriceReporterItem = new javax.swing.JComboBox<>();
        tablePricePostItem = new javax.swing.JComboBox<>();
        jScrollPane3 = new javax.swing.JScrollPane();
        tablePriceTable = new javax.swing.JTable();
        tablePriceAddBtn = new javax.swing.JButton();
        tablePriceSaveBtn = new javax.swing.JButton();
        tablePriceSortByNameBtn = new javax.swing.JButton();
        tablePriceSoryByAmountBtn = new javax.swing.JButton();
        tabelPriceAmountTF = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        salaryReporterItem = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        salaryBtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Ho Ten");

        jLabel2.setText("Dia Chi");

        reporterClassItem.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "chuyen nghiep", "nghiep du", "cong tac vien" }));

        jLabel4.setText("Loai nhan Vien");

        reporterAddBtn.setText("add");

        reporterSaveBtn.setText("save");

        reporterTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Ma nhan vien", "Ho va Ten", "Dia Chi", "Loai Nhan Vien", "Ngay Tham Gia"
            }
        ));
        jScrollPane1.setViewportView(reporterTable);

        jLabel11.setText("Ngay");

        jLabel12.setText("Thang");

        jLabel13.setText("Nam");

        reporterYearCb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reporterYearCbActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(reporterDayCb, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(reporterMonthCb, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGap(9, 9, 9)
                                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(reporterYearCb, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGap(24, 24, 24)
                                .addComponent(jLabel13))))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(87, 87, 87)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(reporterSaveBtn)
                            .addComponent(reporterAddBtn)))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel4)
                            .addComponent(reporterNameTF)
                            .addComponent(reporterAdressTF)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(reporterClassItem, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 87, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 582, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(46, 46, 46)
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(reporterNameTF, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(reporterAdressTF, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(reporterClassItem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(27, 27, 27)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11)
                            .addComponent(jLabel12)
                            .addComponent(jLabel13))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(reporterDayCb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(reporterMonthCb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(reporterYearCb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(30, 30, 30)
                        .addComponent(reporterAddBtn)
                        .addGap(18, 18, 18)
                        .addComponent(reporterSaveBtn))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(60, Short.MAX_VALUE))
        );

        jLabel3.setText("jLabel1");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(277, 277, 277)
                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE)
                .addGap(541, 541, 541))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(jLabel3)
                .addContainerGap(473, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        jTabbedPane1.addTab("PhongVien", jPanel1);

        jLabel5.setText("Ten Kieu Bai");

        jLabel6.setText("Don gia");

        postTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Ma Kieu Bai", "Ten Kieu Bai", "Don Gia", "Ngay Viet"
            }
        ));
        jScrollPane2.setViewportView(postTable);

        postAddBtn.setText("add");

        postSaveBtn.setText("save");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(42, 42, 42)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel6)
                            .addComponent(postClassTF)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 161, Short.MAX_VALUE)
                            .addComponent(postPriceTF)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(73, 73, 73)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(postSaveBtn)
                            .addComponent(postAddBtn))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 195, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(70, 70, 70))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(58, 58, 58)
                        .addComponent(jLabel5)
                        .addGap(18, 18, 18)
                        .addComponent(postClassTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(37, 37, 37)
                        .addComponent(jLabel6)
                        .addGap(18, 18, 18)
                        .addComponent(postPriceTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(31, 31, 31)
                        .addComponent(postAddBtn)
                        .addGap(18, 18, 18)
                        .addComponent(postSaveBtn))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(81, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("BaiViet", jPanel2);

        jLabel7.setText("Ma Nhan vien-Ten Nhan vien");

        jLabel8.setText("Ma Kieu Bai-Ten Kieu Bai");

        tablePriceReporterItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tablePriceReporterItemActionPerformed(evt);
            }
        });

        tablePriceTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Ma nhan vien", "Ten nhan vien", "Ma Kieu Bai", "So Luong Bai"
            }
        ));
        tablePriceTable.setOpaque(false);
        tablePriceTable.setRequestFocusEnabled(false);
        jScrollPane3.setViewportView(tablePriceTable);

        tablePriceAddBtn.setText("add");

        tablePriceSaveBtn.setText("save");

        tablePriceSortByNameBtn.setText("sap xep theo ten");

        tablePriceSoryByAmountBtn.setText("sap xep theo so luong bai viet giam dan");

        jLabel9.setText("so luong bai");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(131, 131, 131)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tablePriceSaveBtn)
                            .addComponent(tablePriceAddBtn)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(43, 43, 43)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(tablePriceSortByNameBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tablePriceSoryByAmountBtn)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(51, 51, 51)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel8)
                                .addComponent(jLabel7)
                                .addComponent(tablePriceReporterItem, 0, 264, Short.MAX_VALUE)
                                .addComponent(tablePricePostItem, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(tabelPriceAmountTF)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 107, Short.MAX_VALUE)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(46, 46, 46))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(60, 60, 60)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tablePriceReporterItem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28)
                        .addComponent(jLabel8)
                        .addGap(18, 18, 18)
                        .addComponent(tablePricePostItem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(23, 23, 23)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tabelPriceAmountTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(17, 17, 17)
                        .addComponent(tablePriceAddBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tablePriceSaveBtn)
                        .addGap(27, 27, 27)
                        .addComponent(tablePriceSortByNameBtn)
                        .addGap(18, 18, 18)
                        .addComponent(tablePriceSoryByAmountBtn))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(64, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("BangTInhCong", jPanel3);

        jLabel10.setText("Ma Nhan vien - ten nhan vien");

        salaryBtn.setText("Tinh Luong");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(57, 57, 57)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10)
                            .addComponent(salaryReporterItem, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(146, 146, 146)
                        .addComponent(salaryBtn)))
                .addContainerGap(557, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(46, 46, 46)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(salaryReporterItem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(salaryBtn)
                .addContainerGap(385, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("BangLuong", jPanel4);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tablePriceReporterItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tablePriceReporterItemActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tablePriceReporterItemActionPerformed

    private void reporterYearCbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reporterYearCbActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_reporterYearCbActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Application.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Application.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Application.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Application.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Application().setVisible(true);
            }
        });
    }
    
    private List<Integer> loadDayMonthYearForList(int valueStart, int valueEnd){
        List<Integer> list = new ArrayList<>();
        for(int value = valueStart; value <= valueEnd; value++){
            list.add(value);
        }
        return list;
    }
    
    private int calculateSalary(Reporter reporter){
        int salary = 0;
        for(TablePrice tablePrice: listTablePrice){
            if(tablePrice.getReporter().getId() == reporter.getId()){
                salary += tablePrice.getAmount() * tablePrice.getPost().getPrice();
            }
        }
        return salary;
    }
    
    private boolean isSameByPostId(Post post, Reporter reporter){
        for(TablePrice tablePrice: listTablePrice){
            if(tablePrice.getReporter().getId() == reporter.getId()){
                if(tablePrice.getPost().getId() == post.getId()){
                    return true;
                }
            }
        }
        return false;
    }
    
    private int countAppearByReporterId(Reporter reporter){
        int count = 0;
        for(TablePrice tablePrice: listTablePrice){
            if(tablePrice.getReporter().getId() == reporter.getId()){
                count++;
            }
        }
        return count;
    }
    
    private IObject getIObject(List<? extends IObject> list, int id){
        for(IObject object: list){
            if(object instanceof Reporter){
                if(((Reporter) object).getId() == id){
                    return object;
                }
            }
            if(object instanceof Post){
                if(((Post) object).getId() == id){
                    return object;
                }
            }
        }
        return null;
    }
    
    private void loadItemsForComboBox(List<? extends Object> list, JComboBox box){
        box.removeAllItems();
        for(Object object: list){
            box.addItem(object.toString());
        }
    }
    
    private boolean checkStringWithPattern(String regex, String input){
        Matcher matcher = Pattern.compile(regex).matcher(input);
        return matcher.matches();
    }
      
    private List<? extends IObject> loadDataToList(String fileName){        
        return (new FileIO(new File(fileName))).readObjectFromFile();
    }
    
    private void writeToFile(List<? extends IObject> list, String fileName){
        new FileIO(new File(fileName)).witeObjectToFile(list);
    }
    
    private void addRowToTable(IObject object, JTable table){
        DefaultTableModel defaultTableModel = (DefaultTableModel) table.getModel();       
        defaultTableModel.addRow(object.toObject());
    }
    
    private void loadDataToTabel(List<? extends IObject> list, JTable table){
        ((DefaultTableModel)table.getModel()).setRowCount(0);
        for(IObject object: list){
            addRowToTable(object, table);
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JButton postAddBtn;
    private javax.swing.JTextField postClassTF;
    private javax.swing.JTextField postPriceTF;
    private javax.swing.JButton postSaveBtn;
    private javax.swing.JTable postTable;
    private javax.swing.JButton reporterAddBtn;
    private javax.swing.JTextField reporterAdressTF;
    private javax.swing.JComboBox<String> reporterClassItem;
    private javax.swing.JComboBox<String> reporterDayCb;
    private javax.swing.JComboBox<String> reporterMonthCb;
    private javax.swing.JTextField reporterNameTF;
    private javax.swing.JButton reporterSaveBtn;
    private javax.swing.JTable reporterTable;
    private javax.swing.JComboBox<String> reporterYearCb;
    private javax.swing.JButton salaryBtn;
    private javax.swing.JComboBox<String> salaryReporterItem;
    private javax.swing.JTextField tabelPriceAmountTF;
    private javax.swing.JButton tablePriceAddBtn;
    private javax.swing.JComboBox<String> tablePricePostItem;
    private javax.swing.JComboBox<String> tablePriceReporterItem;
    private javax.swing.JButton tablePriceSaveBtn;
    private javax.swing.JButton tablePriceSortByNameBtn;
    private javax.swing.JButton tablePriceSoryByAmountBtn;
    private javax.swing.JTable tablePriceTable;
    // End of variables declaration//GEN-END:variables
}
