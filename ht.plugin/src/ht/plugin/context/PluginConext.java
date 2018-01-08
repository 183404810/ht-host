package ht.plugin.context;

import ht.plugin.configration.Configration;
import ht.plugin.configration.Parser;
import ht.plugin.introspect.ITable;

import java.util.List;

public class PluginConext {
	private List<ITable> tables;
	private Configration config;
	private Parser parser;
	private ClassLoader loader;
}
