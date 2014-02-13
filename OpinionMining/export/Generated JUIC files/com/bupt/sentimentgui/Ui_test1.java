/********************************************************************************
** Form generated from reading ui file 'test1.jui'
**
** Created: 星期三 十一月 14 15:20:40 2012
**      by: Qt User Interface Compiler version 4.5.2
**
** WARNING! All changes made in this file will be lost when recompiling ui file!
********************************************************************************/

package com.bupt.sentimentgui;

import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;

public class Ui_test1 implements com.trolltech.qt.QUiForm<QMainWindow>
{
    public QMenuBar menubar;
    public QWidget centralwidget;
    public QStatusBar statusbar;

    public Ui_test1() { super(); }

    public void setupUi(QMainWindow test1)
    {
        test1.setObjectName("test1");
        test1.resize(new QSize(800, 600).expandedTo(test1.minimumSizeHint()));
        menubar = new QMenuBar(test1);
        menubar.setObjectName("menubar");
        test1.setMenuBar(menubar);
        centralwidget = new QWidget(test1);
        centralwidget.setObjectName("centralwidget");
        test1.setCentralWidget(centralwidget);
        statusbar = new QStatusBar(test1);
        statusbar.setObjectName("statusbar");
        test1.setStatusBar(statusbar);
        retranslateUi(test1);

        test1.connectSlotsByName();
    } // setupUi

    void retranslateUi(QMainWindow test1)
    {
        test1.setWindowTitle(com.trolltech.qt.core.QCoreApplication.translate("test1", "MainWindow", null));
    } // retranslateUi

}

