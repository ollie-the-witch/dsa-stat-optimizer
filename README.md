# Ollie's Optimizer for DragonSword Awakening
Last Updated for Patch 1.1.1

Built with Java using Google OR-Tools CP-SAT solver to calculate the equipment needed for some measure of optimal for general cases.


## Running the application

#### From the repository root:

```powershell
mvn clean compile exec:java
```

#### For Windows:

<del>Extract the contents of `portable.zip` and run `"portable\DSA Ollie Optimizer\DSA Ollie Optimizer.exe"`.</del> The zip is too big for GitHub. It is what it is.

You can create/recreate installers or portable versions by following the instructions in `instructions`.

## Build

Compile and package the project:

```powershell
mvn clean package
```

## Clarifications

### Status
For ease of calculations, this program assumes that the enemy will be under at most one physical status (stun/knockdown/airborne) and one elemental status (burn/frost/poison...).

### Uptime
Estimated Status Uptime is for adding weight to Status Attack; as flat Physical/Elemental Status Attack values are so much higher than flat Attack, without adding weight, they will always beat out flat Attack.

A value of `10%` for Physical Uptime means that you estimate that around 10% of your damage will be done while the enemy is under one physical status.


## More Notes
Currently also excludes Status Attack Power as a valid substat because it's so rarely good and removing it decreases calculation time by a lot. Maybe a toggle will be added in a future version?

This also means that extreme off-meta builds that heavily stack status attack values won't be generated here.

Numbers too high will cause integer overflow issues, but for realistic bounds, this should be okay.