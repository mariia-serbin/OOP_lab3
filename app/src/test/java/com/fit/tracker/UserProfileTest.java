package com.fit.tracker;

import com.fit.tracker.domain.UserProfile;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UserProfileTest {
    @Test
    public void defaultProfileIsValid() {
        assertTrue(UserProfile.DEFAULT.isValid());
    }

    @Test
    public void boundaryValuesAreValid() {
        assertTrue(new UserProfile(UserProfile.MIN_HEIGHT_CM, UserProfile.MIN_WEIGHT_KG).isValid());
        assertTrue(new UserProfile(UserProfile.MAX_HEIGHT_CM, UserProfile.MAX_WEIGHT_KG).isValid());
    }

    @Test
    public void outOfRangeValuesAreInvalid() {
        assertFalse(new UserProfile(99, 70.0).isValid());
        assertFalse(new UserProfile(251, 70.0).isValid());
        assertFalse(new UserProfile(170, 29.0).isValid());
        assertFalse(new UserProfile(170, 301.0).isValid());
    }

    @Test
    public void nonFiniteWeightIsInvalid() {
        assertFalse(new UserProfile(170, Double.NaN).isValid());
        assertFalse(new UserProfile(170, Double.POSITIVE_INFINITY).isValid());
    }
}
