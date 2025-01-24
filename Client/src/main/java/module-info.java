module stockmanager.client {
	requires javafx.controls;
	requires javafx.base;
	requires stockmanager.types;
	requires com.fasterxml.jackson.databind;
	requires java.sql;

	exports ch.stockmanager.client;
}