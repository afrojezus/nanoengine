package engine.graph.particle;

import engine.item.GameItem;

import java.util.List;

public interface IParticleEmitter {

    void cleanup();

    Particle getBaseParticle();

    List<GameItem> getParticles();
}