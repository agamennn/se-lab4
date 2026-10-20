package hu.bme.mit.spaceship;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GT4500Test {

    private TorpedoStore primary;
    private TorpedoStore secondary;
    private GT4500 ship;

    @BeforeEach
    public void init() {
        this.primary = mock(TorpedoStore.class);
        this.secondary = mock(TorpedoStore.class);
        this.ship = new GT4500(primary, secondary);
    }

    // ---------------------------------------------------------------- SINGLE

    @Test
    void fireTorpedo_Single_Success() {
        // Arrange
        when(primary.isEmpty()).thenReturn(false);
        when(primary.fire(1)).thenReturn(true);

        // Act
        boolean result = ship.fireTorpedo(FiringMode.SINGLE);

        // Assert
        assertEquals(true, result);
        verify(primary, times(1)).fire(1);
        verify(secondary, never()).fire(1);
    }

    @Test
    void fireTorpedo_Single_AlternatesBetweenStores() {
        // Arrange
        when(primary.isEmpty()).thenReturn(false);
        when(secondary.isEmpty()).thenReturn(false);
        when(primary.fire(1)).thenReturn(true);
        when(secondary.fire(1)).thenReturn(true);

        // Act
        boolean first = ship.fireTorpedo(FiringMode.SINGLE);
        boolean second = ship.fireTorpedo(FiringMode.SINGLE);
        boolean third = ship.fireTorpedo(FiringMode.SINGLE);

        // Assert
        assertEquals(true, first);
        assertEquals(true, second);
        assertEquals(true, third);
        verify(primary, times(2)).fire(1);
        verify(secondary, times(1)).fire(1);
    }

    @Test
    void fireTorpedo_Single_PrimaryEmpty_FiresSecondary() {
        // Arrange
        when(primary.isEmpty()).thenReturn(true);
        when(secondary.isEmpty()).thenReturn(false);
        when(secondary.fire(1)).thenReturn(true);

        // Act
        boolean result = ship.fireTorpedo(FiringMode.SINGLE);

        // Assert
        assertEquals(true, result);
        verify(primary, never()).fire(1);
        verify(secondary, times(1)).fire(1);
    }

    @Test
    void fireTorpedo_Single_SecondaryEmptyAfterPrimary_FiresPrimaryAgain() {
        // Arrange
        when(primary.isEmpty()).thenReturn(false);
        when(secondary.isEmpty()).thenReturn(true);
        when(primary.fire(1)).thenReturn(true);

        // Act
        boolean first = ship.fireTorpedo(FiringMode.SINGLE);
        boolean second = ship.fireTorpedo(FiringMode.SINGLE);

        // Assert
        assertEquals(true, first);
        assertEquals(true, second);
        verify(primary, times(2)).fire(1);
        verify(secondary, never()).fire(1);
    }

    @Test
    void fireTorpedo_Single_BothEmpty_Fails() {
        // Arrange
        when(primary.isEmpty()).thenReturn(true);
        when(secondary.isEmpty()).thenReturn(true);

        // Act
        boolean result = ship.fireTorpedo(FiringMode.SINGLE);

        // Assert
        assertEquals(false, result);
        verify(primary, never()).fire(1);
        verify(secondary, never()).fire(1);
    }

    @Test
    void fireTorpedo_Single_BothEmptyAfterPrimaryFired_Fails() {
        // Arrange: primary has one torpedo, secondary is empty
        when(primary.isEmpty()).thenReturn(false, true);
        when(secondary.isEmpty()).thenReturn(true);
        when(primary.fire(1)).thenReturn(true);

        // Act
        boolean first = ship.fireTorpedo(FiringMode.SINGLE);
        boolean second = ship.fireTorpedo(FiringMode.SINGLE);

        // Assert
        assertEquals(true, first);
        assertEquals(false, second);
        verify(primary, times(1)).fire(1);
        verify(secondary, never()).fire(1);
    }

    @Test
    void fireTorpedo_Single_PrimaryFails_DoesNotTrySecondary() {
        // Arrange
        when(primary.isEmpty()).thenReturn(false);
        when(secondary.isEmpty()).thenReturn(false);
        when(primary.fire(1)).thenReturn(false);

        // Act
        boolean result = ship.fireTorpedo(FiringMode.SINGLE);

        // Assert
        assertEquals(false, result);
        verify(primary, times(1)).fire(1);
        verify(secondary, never()).fire(1);
    }

    // ------------------------------------------------------------------- ALL

    @Test
    void fireTorpedo_All_Success() {
        // Arrange
        when(primary.isEmpty()).thenReturn(false);
        when(secondary.isEmpty()).thenReturn(false);
        when(primary.fire(1)).thenReturn(true);
        when(secondary.fire(1)).thenReturn(true);

        // Act
        boolean result = ship.fireTorpedo(FiringMode.ALL);

        // Assert
        assertEquals(true, result);
        verify(primary, times(1)).fire(1);
        verify(secondary, times(1)).fire(1);
    }

    @Test
    void fireTorpedo_All_PrimaryEmpty_FiresSecondaryOnly() {
        // Arrange
        when(primary.isEmpty()).thenReturn(true);
        when(secondary.isEmpty()).thenReturn(false);
        when(secondary.fire(1)).thenReturn(true);

        // Act
        boolean result = ship.fireTorpedo(FiringMode.ALL);

        // Assert
        assertEquals(true, result);
        verify(primary, never()).fire(1);
        verify(secondary, times(1)).fire(1);
    }

    @Test
    void fireTorpedo_All_SecondaryEmpty_FiresPrimaryOnly() {
        // Arrange
        when(primary.isEmpty()).thenReturn(false);
        when(secondary.isEmpty()).thenReturn(true);
        when(primary.fire(1)).thenReturn(true);

        // Act
        boolean result = ship.fireTorpedo(FiringMode.ALL);

        // Assert
        assertEquals(true, result);
        verify(primary, times(1)).fire(1);
        verify(secondary, never()).fire(1);
    }

    @Test
    void fireTorpedo_All_BothEmpty_Fails() {
        // Arrange
        when(primary.isEmpty()).thenReturn(true);
        when(secondary.isEmpty()).thenReturn(true);

        // Act
        boolean result = ship.fireTorpedo(FiringMode.ALL);

        // Assert
        assertEquals(false, result);
        verify(primary, never()).fire(1);
        verify(secondary, never()).fire(1);
    }

    @Test
    void fireTorpedo_All_BothFail_ReturnsFalse() {
        // Arrange
        when(primary.isEmpty()).thenReturn(false);
        when(secondary.isEmpty()).thenReturn(false);
        when(primary.fire(1)).thenReturn(false);
        when(secondary.fire(1)).thenReturn(false);

        // Act
        boolean result = ship.fireTorpedo(FiringMode.ALL);

        // Assert
        assertEquals(false, result);
        verify(primary, times(1)).fire(1);
        verify(secondary, times(1)).fire(1);
    }

    @Test
    void fireTorpedo_All_OnlySecondarySucceeds_ReturnsTrue() {
        // Arrange
        when(primary.isEmpty()).thenReturn(false);
        when(secondary.isEmpty()).thenReturn(false);
        when(primary.fire(1)).thenReturn(false);
        when(secondary.fire(1)).thenReturn(true);

        // Act
        boolean result = ship.fireTorpedo(FiringMode.ALL);

        // Assert
        assertEquals(true, result);
    }

    // ----------------------------------------------------------------- LASER

    @Test
    void fireLaser_NotImplemented_ReturnsFalse() {
        // Act
        boolean result = ship.fireLaser(FiringMode.SINGLE);

        // Assert
        assertEquals(false, result);
        verify(primary, never()).fire(1);
        verify(secondary, never()).fire(1);
    }
}
