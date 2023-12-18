package me.ChristopherW.core.custom.Animations;

import me.ChristopherW.core.Animation;

public class AnimationExample extends Animation {

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

    @Override
    public void tick(float time) {
        if(!isPlaying)
            return;
            
        timeElapsed += time;
    }

    @Override
    public void play() {
        this.isPlaying = true;
        this.timeElapsed = 0;
    }
    
}
