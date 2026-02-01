package co.istad.springdatajpa.repository;

import co.istad.springdatajpa.entity.AttributeDefinition;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttributeDefinitionRepository extends JpaRepository<AttributeDefinition, UUID> {
}
