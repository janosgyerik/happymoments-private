package com.happymoments;

import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Test;
import org.mockito.InOrder;


public class HappyMomentsSQLiteOpenHelperTest {

	@Test
	public void testUpgradeFrom1To2() {
		HappyMomentsSQLiteOpenHelper helper = mock(HappyMomentsSQLiteOpenHelper.class);

		doCallRealMethod().when(helper).onUpgrade(null, 1, 2);
		helper.onUpgrade(null, 1, 2);
		
		InOrder inOrder = inOrder(helper);
		inOrder.verify(helper).onUpgrade(null, 1, 2);
		inOrder.verify(helper).upgradeToVersion(null, 2);
		verifyNoMoreInteractions(helper);
	}

	@Test
	public void testUpgradeFrom1To3() {
		HappyMomentsSQLiteOpenHelper helper = mock(HappyMomentsSQLiteOpenHelper.class);

		doCallRealMethod().when(helper).onUpgrade(null, 1, 3);
		helper.onUpgrade(null, 1, 3);
		
		InOrder inOrder = inOrder(helper);
		inOrder.verify(helper).onUpgrade(null, 1, 3);
		inOrder.verify(helper).upgradeToVersion(null, 2);
		inOrder.verify(helper).upgradeToVersion(null, 3);
		verifyNoMoreInteractions(helper);
	}

	@Test
	public void testUpgradeFrom1To4() {
		HappyMomentsSQLiteOpenHelper helper = mock(HappyMomentsSQLiteOpenHelper.class);

		doCallRealMethod().when(helper).onUpgrade(null, 1, 4);
		helper.onUpgrade(null, 1, 4);
		
		InOrder inOrder = inOrder(helper);
		inOrder.verify(helper).onUpgrade(null, 1, 4);
		inOrder.verify(helper).upgradeToVersion(null, 2);
		inOrder.verify(helper).upgradeToVersion(null, 3);
		inOrder.verify(helper).upgradeToVersion(null, 4);
		verifyNoMoreInteractions(helper);
	}

	@Test
	public void testUpgradeFrom2To4() {
		HappyMomentsSQLiteOpenHelper helper = mock(HappyMomentsSQLiteOpenHelper.class);

		doCallRealMethod().when(helper).onUpgrade(null, 2, 4);
		helper.onUpgrade(null, 2, 4);
		
		InOrder inOrder = inOrder(helper);
		inOrder.verify(helper).onUpgrade(null, 2, 4);
		inOrder.verify(helper).upgradeToVersion(null, 3);
		inOrder.verify(helper).upgradeToVersion(null, 4);
		verifyNoMoreInteractions(helper);
	}

	@Test
	public void testUpgradeFrom3To4() {
		HappyMomentsSQLiteOpenHelper helper = mock(HappyMomentsSQLiteOpenHelper.class);

		doCallRealMethod().when(helper).onUpgrade(null, 3, 4);
		helper.onUpgrade(null, 3, 4);
		
		InOrder inOrder = inOrder(helper);
		inOrder.verify(helper).onUpgrade(null, 3, 4);
		inOrder.verify(helper).upgradeToVersion(null, 4);
		verifyNoMoreInteractions(helper);
	}

}
