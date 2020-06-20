package hackit.model;

import org.jetbrains.annotations.NotNull;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.UUID;

@MappedSuperclass
public abstract class AbstractEntity implements Serializable {

    @Id
    @NotNull
    protected String id = UUID.randomUUID().toString();

    public AbstractEntity() {
    }

    @NotNull
    public String getId() {
        return this.id;
    }

    public void setId(@NotNull final String id) {
        this.id = id;
    }
}
