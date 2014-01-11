package org.magnolialang.backend;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class MagnoliaBackendPlugin implements BundleActivator {
	private static BundleContext context;
	public static final String PLUGIN_ID = "magnolia-back";

	public MagnoliaBackendPlugin() {

	}

	static BundleContext getContext() {
		return context;
	}

	@Override
	public void start(BundleContext bundleContext) throws Exception {
		MagnoliaBackendPlugin.context = bundleContext;
	}

	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		MagnoliaBackendPlugin.context = null;
	}

}
