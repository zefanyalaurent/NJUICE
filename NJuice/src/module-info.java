module NJuice {
	exports main;
	opens main;
	exports dashboard;
	opens dashboard;
	exports user;
	opens user;
	exports admin;
	opens admin;

	requires javafx.base;
	requires javafx.controls;
	requires javafx.graphics;
	requires java.sql;
	requires java.desktop;
}