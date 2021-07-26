package unikom.gery.damang.data;

import java.util.ArrayList;
import java.util.Random;

import unikom.gery.damang.R;
import unikom.gery.damang.model.Cardio;

public class CardioList {
    private static String[] cardioNames = {
            "Curtsy Lunge",
            "Bicycle Crunch",
            "Mountain Climbers",
            "Jumping Jacks",
            "Stutter Steps",
            "Squat",
            "Flutter Kick",
            "Crunches",
            "AB Curls",
            "Standing Elbow to Knee",
            "Invisible Jump Rope",
            "Arm Cross Side Lunge",
    };

    private static int[] cardioAnimations = {
            R.raw.crusty_lunges,
            R.raw.bicycle_crunches,
            R.raw.mountain_climbers,
            R.raw.jumping_jack,
            R.raw.stutter_steps,
            R.raw.squat,
            R.raw.flutter_kick,
            R.raw.crunches,
            R.raw.ab_curls,
            R.raw.standing_elbow,
            R.raw.invisible_jump,
            R.raw.arm_cross_side_lunge
    };

    public static ArrayList<Cardio> getCardioList(String level) {
        ArrayList<Cardio> list = new ArrayList<>();
        int length;
        if (level.equals("Pemula (5 - 10 menit)"))
            length = 8;
        else if (level.equals("Menengah (10 - 20 menit)"))
            length = 12;
        else
            length = 16;
        for (int position = 0; position < length; position++) {
            Random random = new Random();
            int randomNumber = random.nextInt(cardioNames.length);
            Cardio cardio = new Cardio();
            cardio.setName(cardioNames[randomNumber]);
            cardio.setAnimation(cardioAnimations[randomNumber]);
            cardio.setCount(16);
            list.add(cardio);
        }
        return list;
    }
}
