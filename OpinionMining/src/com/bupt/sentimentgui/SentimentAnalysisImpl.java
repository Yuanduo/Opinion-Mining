package com.bupt.sentimentgui;

import com.trolltech.qt.gui.*;

public class SentimentAnalysisImpl extends QDialog {

	SentimentAnalysis ui = new SentimentAnalysis();

	public static void main(String[] args) {
		QApplication.initialize(args);

		SentimentAnalysisImpl testsentimentAnalysisImpl = new SentimentAnalysisImpl();
		testsentimentAnalysisImpl.show();

		QApplication.exec();
	}

	public SentimentAnalysisImpl() {
		ui.setupUi(this);
	}

	public SentimentAnalysisImpl(QWidget parent) {
		super(parent);
		ui.setupUi(this);
	}
}
