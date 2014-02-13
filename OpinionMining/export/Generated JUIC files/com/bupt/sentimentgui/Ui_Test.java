/********************************************************************************
** Form generated from reading ui file 'Test.jui'
**
** Created: 星期三 十一月 14 15:19:32 2012
**      by: Qt User Interface Compiler version 4.5.2
**
** WARNING! All changes made in this file will be lost when recompiling ui file!
********************************************************************************/

package com.bupt.sentimentgui;

import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;

public class Ui_Test implements com.trolltech.qt.QUiForm<QDialog>
{
    public QDialogButtonBox buttonBox;

    public Ui_Test() { super(); }

    public void setupUi(QDialog Test)
    {
        Test.setObjectName("Test");
        Test.resize(new QSize(400, 300).expandedTo(Test.minimumSizeHint()));
        buttonBox = new QDialogButtonBox(Test);
        buttonBox.setObjectName("buttonBox");
        buttonBox.setGeometry(new QRect(30, 240, 341, 31));
        buttonBox.setFocusPolicy(com.trolltech.qt.core.Qt.FocusPolicy.TabFocus);
        buttonBox.setStandardButtons(com.trolltech.qt.gui.QDialogButtonBox.StandardButton.createQFlags(com.trolltech.qt.gui.QDialogButtonBox.StandardButton.Cancel,com.trolltech.qt.gui.QDialogButtonBox.StandardButton.NoButton,com.trolltech.qt.gui.QDialogButtonBox.StandardButton.Ok));
        retranslateUi(Test);

        Test.connectSlotsByName();
    } // setupUi

    void retranslateUi(QDialog Test)
    {
        Test.setWindowTitle(com.trolltech.qt.core.QCoreApplication.translate("Test", "Dialog", null));
    } // retranslateUi

}

