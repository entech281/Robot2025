package frc.robot.subsystems.led;

import edu.wpi.first.wpilibj.util.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an LED string that is subdivided into multiple sections.
 * Each section can be configured with foreground and background colors and blinking.
 */
public class SubdividedLedString {

  /** List of LED sections in the string. */
  private final List<LedSection> sections = new ArrayList<>();

  /**
   * Adds a new section to the LED string.
   *
   * @param fgColor   the foreground color when the section is active
   * @param bgColor   the background color when the section is off
   * @param startIndex the starting index (inclusive) of the section on the LED strip
   * @param endIndex  the ending index (exclusive) of the section on the LED strip
   * @return the newly created {@link LedSection}
   */
  public LedSection addSection(Color fgColor, Color bgColor, int startIndex, int endIndex) {
      LedSection s = new LedSection(fgColor, bgColor, startIndex, endIndex, false);
      sections.add(s);
      return s;
  }

  /**
   * Returns the list of LED sections.
   *
   * @return a list of {@link LedSection} objects
   */
  public List<LedSection> getSections() {
    return sections;
  }

  /**
   * Converts the LED sections to a list of colors.
   * Each section is expanded into its respective color across the range from startIndex to endIndex.
   *
   * @return a list of {@link Color} representing the LED colors for each index in the string
   */
  public List<Color> toColorList(){
    List<Color> r = new ArrayList<>();
    for (LedSection s : sections) {
        for (int i = s.startIndex; i < s.endIndex; i++){
          r.add(s.fgColor);
        }
    }
    return r;
  }



  @Override
  public String toString() {
    return "SubdividedLedString [sections=" + sections + "]";
  }



  /**
   * Represents a section of the subdivided LED string.
   * A section handles its own foreground, background, and current color along with blinking state.
   */
  public static class LedSection {

      /** Starting index (inclusive) of this section. */
      private int startIndex;
      
      /** Ending index (exclusive) of this section. */
      private int endIndex;

      /** Foreground color when the section is active. */
      private Color fgColor;
      
      /** Background color when the section is turned off. */
      private Color bgColor;
      
      /** The current color of the section. */
      private Color currentColor;
      
      /** Whether this section is blinking. */
      boolean blinking = false;

      /**
       * Constructs a new LED section.
       *
       * @param fgColor    the foreground color when active
       * @param bgColor    the background color when off
       * @param startIndex the starting index (inclusive) of the section
       * @param endIndex   the ending index (exclusive) of the section
       * @param blinking   whether the section should blink
       */
      public LedSection(Color fgColor, Color bgColor, int startIndex, int endIndex, boolean blinking) {
          this.fgColor = fgColor;
          this.bgColor = bgColor;
          this.startIndex = startIndex;
          this.endIndex = endIndex;
          this.blinking = blinking;
          this.currentColor = fgColor;
      }

      /**
       * Returns the foreground color.
       *
       * @return foreground {@link Color}
       */
      public Color getFgColor() {
        return fgColor;
      }

      /**
       * Sets the current color of the section to the foreground color.
       */
      public void on() {
        this.currentColor = this.fgColor;
      }

      /**
       * Sets the current color of the section to the background color.
       */
      public void off() {
        this.currentColor = this.bgColor;
      }

      /**
       * Sets the foreground color.
       *
       * @param fgColor the new foreground {@link Color}
       */
      public void setFgColor(Color fgColor) {
        this.fgColor = fgColor;
      }

      /**
       * Returns the background color.
       *
       * @return background {@link Color}
       */
      public Color getBgColor() {
        return bgColor;
      }

      /**
       * Sets the background color.
       *
       * @param bgColor the new background {@link Color}
       */
      public void setBgColor(Color bgColor) {
        this.bgColor = bgColor;
      }

      /**
       * Returns whether this section is blinking.
       *
       * @return true if blinking, false otherwise
       */
      public boolean isBlinking() {
        return blinking;
      }

      /**
       * Sets the blinking state of this section.
       *
       * @param blinking true to enable blinking, false to disable
       */
      public void setBlinking(boolean blinking) {
        this.blinking = blinking;
      }

      /**
       * Returns the current color.
       *
       * @return the {@link Color} currently being displayed in the section
       */
      public Color getCurrentColor() {
        return currentColor;
      }

      /**
       * Returns the starting index of this section in the LED strip.
       *
       * @return the starting index of this section
       */
      public int getStartIndex() {
        return startIndex;
      }

      /**
       * Returns the ending index of this section in the LED strip.
       *
       * @return the ending index of this section
       */
      public int getEndIndex() {
        return endIndex;
      }

      @Override
      public String toString() {
        return "LedSection [startIndex=" + startIndex + ", endIndex=" + endIndex + ", fgColor=" + fgColor + ", bgColor="
            + bgColor + ", currentColor=" + currentColor + ", blinking=" + blinking + "]";
      }
      
  }
}