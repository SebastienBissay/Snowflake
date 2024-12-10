import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

import static parameters.Parameters.*;
import static save.SaveUtil.saveSketch;

public class Snowflake extends PApplet {

    private List<PVector> points;

    public static void main(String[] args) {
        PApplet.main(Snowflake.class);
    }

    @Override
    public void settings() {
        size(WIDTH, HEIGHT);
        randomSeed(SEED);
        noiseSeed(floor(random(MAX_INT)));
    }

    @Override
    public void setup() {
        background(BACKGROUND_COLOR.red(), BACKGROUND_COLOR.green(), BACKGROUND_COLOR.blue());
        stroke(STROKE_COLOR.red(), STROKE_COLOR.green(), STROKE_COLOR.blue(), STROKE_COLOR.alpha());
        noFill();

        points = new ArrayList<>();
        for (int k = 0; k < NUMBER_OF_STARTING_POINTS; k++) {
            points.add(PVector.fromAngle(random(TWO_PI))
                    .mult(STARTING_DISTANCE_FACTOR * pow(random(1), STARTING_DISTANCE_EXPONENT))
                    .add(ORIGIN.x, ORIGIN.y));
        }
    }

    @Override
    public void draw() {
        points.forEach(point -> {
            point.add(PVector.fromAngle(sq(PI) * noise((point.x - ORIGIN.x) * NOISE_SCALE, (point.y - ORIGIN.y) * NOISE_SCALE))
                    .mult(LENGTH_VARIANCE * randomGaussian()));
            snowflakeLine(point, ORIGIN);
        });
        if (frameCount >= NUMBER_OF_ITERATIONS) {
            noLoop();
            saveSketch(this);
        }
    }

    private void snowflakeLine(PVector start, PVector end) {
        PVector v = PVector.sub(start, end);
        float angle = v.heading();
        if (angle < 0) {
            angle += TWO_PI;
        }
        PVector A = PVector.fromAngle(angle % (PI / 4)).mult(v.mag());
        PVector B;
        if (2 * sq(A.y) > sq(A.x - A.y)) {
            B = new PVector(A.y, A.y);
        } else {
            B = new PVector(A.x - A.y, 0);
        }
        int n = floor(angle / (QUARTER_PI));

        B.rotate(n * QUARTER_PI + ROTATION_VARIANCE * randomGaussian());
        B.add(end);
        line(B.x, B.y, start.x, start.y);
        randomLine(B, start);
    }

    private void randomLine(PVector A, PVector B) {
        float d = PVector.sub(A, B).mag();
        for (int i = 0; i < d / 2; i++) {
            PVector p = PVector.lerp(A, B, random(1));
            point(p.x, p.y);
        }
    }
}
