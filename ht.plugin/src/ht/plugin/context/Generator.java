package ht.plugin.context;

import ht.plugin.generate.GeneratedFile;

import java.util.List;

public interface Generator {
	public void generate(List<GeneratedFile> list,List<String> tableList);
}
