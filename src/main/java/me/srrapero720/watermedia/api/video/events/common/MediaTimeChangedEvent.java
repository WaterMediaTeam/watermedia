package me.srrapero720.watermedia.api.video.events.common;

import me.srrapero720.watermedia.api.video.VideoPlayer;
import java.util.Objects;

public interface MediaTimeChangedEvent<P extends VideoPlayer> extends Event<MediaTimeChangedEvent.EventData, P> {
    class EventData {
        private final long beforeTime;
        private final long afterTime;

        public EventData(long beforeTime, long afterTime) {
            this.beforeTime = beforeTime;
            this.afterTime = afterTime;
        }

        public long getBeforeTime() {
            return beforeTime;
        }

        public long getAfterTime() {
            return afterTime;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            EventData eventData = (EventData) o;
            return beforeTime == eventData.beforeTime &&
                    afterTime == eventData.afterTime;
        }

        @Override
        public int hashCode() {
            return Objects.hash(beforeTime, afterTime);
        }

        @Override
        public String toString() {
            return "EventData{" +
                    "beforeTime=" + beforeTime +
                    ", afterTime=" + afterTime +
                    '}';
        }
    }
}
