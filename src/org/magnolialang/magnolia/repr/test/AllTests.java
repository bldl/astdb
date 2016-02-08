package org.magnolialang.magnolia.repr.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ EntryMapTests.class, EntryTests.class, MongoAstTests.class, NodeTests.class })
public class AllTests {

}
