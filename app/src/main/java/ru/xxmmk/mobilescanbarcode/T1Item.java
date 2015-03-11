package ru.xxmmk.mobilescanbarcode;

/**
 * Created by zemljanskij111614 on 20.02.2015.
 */
public class T1Item {
    /**
     * Заголовок
     */
    String header;
    /**
     * Подзаголовок
     */
    String subHeader;   //наименование
    String subHeader1;  //сертификат
    String isChecked;   //флаг показывающий провереное или нет
    String PlavkN;      //номер плавки
    String PartNum;     //номер партии
    String PackNum;     //номер упаковки
    String Weight;      //вес
    String LongName;    //наименование полное
    //String CertNum;
    /**
     * Конструктор создает новый элемент в соответствии с передаваемыми
     * параметрами:
     * @param h - заголовок элемента
     * @param s - подзаголовок
     */
    T1Item(String h, String s,  String s1, String s2,String s3 ,String s4,String s5,String s6,String s7){
        this.header     =h;
        this.subHeader  =s;
        this.subHeader1 =s1;
        this.isChecked  =s2;
        this.PlavkN     =s3;
        this.PartNum    =s4;
        this.PackNum    =s5;
        this.Weight     =s6;
        this.LongName     =s7;

    }

    //Всякие гетеры и сеттеры
    public String getHeader() {
        return header;
    }
    public void setHeader(String header) {
        this.header = header;
    }
    public String getSubHeader() {
        return subHeader;
    }
    public void setSubHeader(String subHeader) {
        this.subHeader = subHeader;
    }
    public String getSubHeader1() {
        return subHeader1;
    }
    public void setSubHeader1(String subHeader) {
        this.subHeader1 = subHeader1;
    }
    public String getChecked() {
        return isChecked;
    }
    public   void setChecked    (String isChecked) {
        this.isChecked = isChecked;
    }


    public String getPlavkN() {
        return PlavkN;
    }
    public void setPlavkN(String PlavkN) {
        this.PlavkN = PlavkN;
    }
    public String getPartNum() {
        return PartNum;
    }
    public void setPartNum(String PartNum) {
        this.PartNum = PartNum;
    }
    public String getPackNum() {
        return PackNum;
    }
    public void setPackNum(String PackNum) {
        this.PackNum = PackNum;
    }
    public String getWeight() {
        return Weight;
    }
    public void setWeight(String Weight) {
        this.Weight = Weight;
    }
    public String getLongName() {
        return LongName;
    }
    public void setLongName(String LongName) {
        this.LongName = LongName;
    }
}