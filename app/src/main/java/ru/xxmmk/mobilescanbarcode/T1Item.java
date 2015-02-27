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
    String subHeader;
    String subHeader1;
    String isChecked;
    /**
     * Конструктор создает новый элемент в соответствии с передаваемыми
     * параметрами:
     * @param h - заголовок элемента
     * @param s - подзаголовок
     */
    T1Item(String h, String s,  String s1, String s2){
        this.header=h;
        this.subHeader=s;
        this.subHeader1=s1;
        this.isChecked=s2;

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

}