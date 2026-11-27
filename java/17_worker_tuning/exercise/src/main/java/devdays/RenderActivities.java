package devdays;

import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface RenderActivities {
    String render(String item);
}
