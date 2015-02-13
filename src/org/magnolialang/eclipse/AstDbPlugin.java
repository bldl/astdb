package org.magnolialang.eclipse;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class AstDbPlugin implements BundleActivator {
	private static BundleContext context;
	public static final String PLUGIN_ID = "astdb";


	static BundleContext getContext() {
		return context;
	}


	public AstDbPlugin() {
	}


	@Override
	public void start(BundleContext bundleContext) throws Exception {
		AstDbPlugin.context = bundleContext;
	}


	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		AstDbPlugin.context = null;
	}

}
