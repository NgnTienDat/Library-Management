# ADR-XXX: [Short Title]

## Status

[Proposed | Accepted | Deprecated | Superseded by ADR-YYY]

## Date

YYYY-MM-DD

## Context

[Describe the issue that motivated this decision. What is the problem we are trying to solve?]

## Decision Drivers

- [Driver 1]
- [Driver 2]
- [Driver 3]

## Considered Options

### Option 1: [Name]
**Description**: [Brief description]

**Pros**:
- [Pro 1]
- [Pro 2]

**Cons**:
- [Con 1]
- [Con 2]

### Option 2: [Name]
**Description**: [Brief description]

**Pros**:
- [Pro 1]
- [Pro 2]

**Cons**:
- [Con 1]
- [Con 2]

### Option 3: [Name]
**Description**: [Brief description]

**Pros**:
- [Pro 1]
- [Pro 2]

**Cons**:
- [Con 1]
- [Con 2]

## Decision

We decided on **[Option X]** because [rationale].

## Consequences

### Positive
- [Positive consequence 1]
- [Positive consequence 2]

### Negative
- [Negative consequence 1]
- [Negative consequence 2]

### Neutral
- [Neutral consequence]

## Related Decisions

- [ADR-XXX: Related decision]

## References

- [Link to relevant documentation]
- [Link to relevant discussion]

---

## Example ADR

# ADR-001: Use PostgreSQL as Primary Database

## Status
Accepted

## Date
2024-01-15

## Context
We need to choose a primary database for our e-commerce platform. The database needs to handle:
- High read/write throughput
- Complex queries for reporting
- ACID transactions for orders
- Scale to 1M+ products

## Decision Drivers
- Need for relational data with complex relationships
- Requirement for ACID transactions
- Team familiarity
- Open source preference
- Cloud compatibility

## Considered Options

### Option 1: PostgreSQL
**Pros**: ACID, JSON support, mature, open source, excellent performance
**Cons**: Horizontal scaling complexity

### Option 2: MySQL
**Pros**: Popular, well-supported, familiar
**Cons**: Less feature-rich, JSON support not as good

### Option 3: MongoDB
**Pros**: Flexible schema, horizontal scaling
**Cons**: Not ideal for relational data, eventual consistency

## Decision
We decided on **PostgreSQL** because:
- Strong ACID guarantees for order transactions
- Excellent JSON support for flexible product attributes
- Team has experience with PostgreSQL
- Good cloud support (RDS, Cloud SQL)
- Rich ecosystem of tools

## Consequences

### Positive
- Reliable transactions for orders
- Flexible querying for analytics
- Well-documented and supported

### Negative
- Need to plan for read replicas for scaling
- More complex sharding if needed later

## References
- PostgreSQL documentation: https://www.postgresql.org/docs/