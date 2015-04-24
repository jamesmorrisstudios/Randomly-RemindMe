/*
 * Copyright (c) 2015.  James Morris Studios
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jamesmorrisstudios.com.randremind.utilities;

import android.animation.ObjectAnimator;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Animator helper functions
 *
 * Created by James on 4/24/2015.
 */
public class AnimatorControl {

    /**
     * Internal generic object animator builder
     *
     * @param field      The field to animate on
     * @param view       The view to run the animation on
     * @param start      The starting value
     * @param end        The ending value
     * @param duration   The duration of the animation in milliseconds
     * @param startDelay The delay before starting the animation in milliseconds
     * @return The ObjectAnimator. You must still start it.
     */
    @NonNull
    private static ObjectAnimator buildAnimatorLinear(@NonNull String field, @NonNull View view, float start, float end, long duration, long startDelay) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(view, field, start, end);
        anim.setInterpolator(new LinearInterpolator());
        if (duration != 0) {
            anim.setDuration(duration);
        }
        if (startDelay != 0) {
            anim.setStartDelay(startDelay);
        }
        return anim;
    }

    /**
     * @param view       The view to run the animation on
     * @param startAlpha The starting value
     * @param endAlpha   The ending value
     * @param duration   The duration of the animation in milliseconds
     * @param startDelay The delay before starting the animation in milliseconds
     * @return The ObjectAnimator. You must still start it.
     */
    @NonNull
    public static ObjectAnimator alpha(@NonNull View view, float startAlpha, float endAlpha, long duration, long startDelay) {
        return buildAnimatorLinear("alpha", view, startAlpha, endAlpha, duration, startDelay);
    }

}
