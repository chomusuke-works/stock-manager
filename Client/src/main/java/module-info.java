module stockmanager.client {
	requires javafx.controls;
	requires javafx.base;
	requires stockmanager.types;
	requires com.fasterxml.jackson.databind;

	exports ch.stockmanager.client;
}