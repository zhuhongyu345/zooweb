package com.zhy.zooweb.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TreeRoot extends ArrayList<Tree>{

	private static final long serialVersionUID = -5814745768637302855L;

	public TreeRoot() {
		Map<String, Object> atr = new HashMap<String, Object>();
		atr.put("path", "/");
		Tree root = new Tree(0, "/", Tree.STATE_CLOSED, null, atr);
		this.add(root);
	}

}
