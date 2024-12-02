package backend.academy;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Pixel {
    private int r;
    private int g;
    private int b;
    private int hitCount;
    private double normal;

    public void incrementHitCount() {
        hitCount++;
    }
}
