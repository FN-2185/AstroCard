#  AstroCard — Natal Chart Telegram Bot

**AstroCard** is a Telegram bot developed in Java that generates personalized astrological natal charts based on a user's birth date, time, and location.

The application uses astronomical calculations to determine planetary positions, zodiac signs, astrological houses, and important chart angles.

Planetary positions and house cusps are calculated using the **Swiss Ephemeris** library, while astrological interpretations are retrieved from a PostgreSQL database.

##  How Does AstroCard Work?

To generate a natal chart, the user provides the following information:

1. **Country of birth** — used to identify the geographical region.
2. **City of birth** — used to determine geographical latitude, longitude, and time zone.
3. **Date of birth** — entered in `DD.MM.YYYY` format.
4. **Time of birth** — entered in `HH:MM` format.

After collecting the information, AstroCard calculates the natal chart and allows the user to explore planetary placements and their interpretations.

##  Features

- Calculation of planetary positions in the zodiac.
- Determination of planets in astrological houses.
- Support for the Placidus house system.
- Textual interpretations of planetary placements.
- Interactive navigation through Telegram buttons.

##  Screenshots

### Start
<p>
  <img src="images/1.jpg" width="250" alt="Natal Chart">
  <img src="images/2.jpg" width="250" alt="Natal Chart">
</p>

---

### Input
<p>
  <img src="images/3.jpg" width="250" alt="Natal Chart">
  <img src="images/7.jpg" width="250" alt="Natal Chart">
  <img src="images/4.jpg" width="250" alt="Natal Chart">
</p>

---

### Natal Chart Results
![Natal Chart](images/5.jpg)

---

### Forecast
![Forecast](images/6.jpg)

---

### Description
<p>
  <img src="images/8.jpg" width="250" alt="Natal Chart">
  <img src="images/9.jpg" width="250" alt="Natal Chart">
</p>

---

##  Technology Stack

- **Java 21** — core programming language.
- **Spring Boot** — application framework.
- **Telegram Bots API** — Telegram integration.
- **Swiss Ephemeris** — astronomical calculations.
- **PostgreSQL** — storage of geographical data and astrological descriptions.
- **Spring StateMachine** — management of user interaction states.

---

##  How Is a Natal Chart Calculated?

A natal chart represents the apparent positions of celestial bodies in the sky at a particular moment and geographical location.

The calculation consists of several stages.

### 1. Birth Time and Geographical Coordinates

The application retrieves the geographical coordinates of the selected city:

- Latitude (φ)
- Longitude (λ)
- Historical time zone information

The user's local birth time is converted to Coordinated Universal Time (UTC), taking historical daylight-saving time changes into account.

UTC is then converted to a Julian Day, which is used by Swiss Ephemeris to calculate planetary positions.

### 2. Planetary Motion and Positions

The planets orbit the Sun at different speeds and distances.

However, an astrological natal chart normally uses a **geocentric perspective**, meaning that planetary positions are calculated as they appear from Earth.

Because Earth and the other planets are continuously moving, their apparent positions against the background of the zodiac change over time.

Swiss Ephemeris calculates the apparent geocentric ecliptic longitude and latitude of each planet for the specified moment.

The main coordinate used for zodiac placement is **ecliptic longitude**, measured in degrees along the ecliptic from 0° to 360°.

### 3. The Vernal Equinox and the Zodiac

The ecliptic is the apparent annual path of the Sun across the celestial sphere.

Earth's equatorial plane intersects the ecliptic at two points, corresponding to the equinoxes.

The **vernal equinox** is the point where the Sun crosses the celestial equator from south to north.

In the tropical zodiac, this point defines **0° Aries**, the beginning of the zodiac.

Due to the precession of Earth's rotational axis, the equinox gradually shifts relative to the background stars. The tropical zodiac remains anchored to the equinox rather than to fixed constellations.

The zodiac is divided into 12 equal sections of 30° each.

| Zodiac Sign | Ecliptic Longitude |
|---|---|
| Aries | 0°–30° |
| Taurus | 30°–60° |
| Gemini | 60°–90° |
| Cancer | 90°–120° |
| Leo | 120°–150° |
| Virgo | 150°–180° |
| Libra | 180°–210° |
| Scorpio | 210°–240° |
| Sagittarius | 240°–270° |
| Capricorn | 270°–300° |
| Aquarius | 300°–330° |
| Pisces | 330°–360° |

A planet's zodiac sign is determined by dividing its ecliptic longitude by 30°:

`signIndex = floor(longitude / 30)`

For example, a planet at 195° ecliptic longitude is located at 15° Libra.

### 4. Earth's Rotation and the Ascendant

While planets move along their orbits, Earth rotates around its own axis approximately once every sidereal day.

This rotation causes different regions of the celestial sphere to rise above and set below the local horizon.

The **Ascendant (ASC)** is the point where the ecliptic intersects the eastern horizon at the moment of birth.

The **Descendant (DSC)** is the opposite point on the western horizon.

The **Midheaven (MC)** is the intersection of the ecliptic with the local upper meridian, while the **Imum Coeli (IC)** is the opposite point.

These angles depend on:

- Birth time
- Geographical latitude
- Geographical longitude
- Earth's rotation and the orientation of the celestial sphere

Even a relatively small difference in birth time can significantly change the Ascendant and house positions.

### 5. Calculating Houses Using the Placidus System

AstroCard uses the **Placidus house system**, one of the commonly used systems in Western astrology.

Unlike the zodiac signs, which always occupy exactly 30° each, astrological houses in Placidus generally have unequal angular sizes.

The Placidus system is based on the diurnal motion of the celestial sphere and divides the time associated with celestial bodies moving through specific arcs above and below the horizon.

The four principal angles establish the main chart divisions:

- **ASC** — cusp of the 1st house.
- **IC** — cusp of the 4th house.
- **DSC** — cusp of the 7th house.
- **MC** — cusp of the 10th house.

In total 12 houses

The intermediate house cusps are calculated using the Placidus time-division method.

For example, the diurnal arc between the eastern horizon and upper meridian is divided into three equal time intervals to determine intermediate cusps in that quadrant.

The resulting points are projected onto the ecliptic to obtain the corresponding house cusp longitudes.

This process is repeated for the other quadrants.

Since the ecliptic and horizon intersect at different angles depending on geographical latitude and sidereal time, the resulting houses are not generally equal in size.

**Important:** The Placidus system can become undefined for certain house cusps at extreme geographical latitudes, where the required rising and setting conditions do not exist.

### 6. Assigning Planets to Houses

After calculating the twelve house cusps, the application determines which astrological house contains each planet.

Each planet has an ecliptic position, while each house occupies a region of the chart defined by its cusps.

For example:

- Sun in Libra, 1st house
- Moon in Leo, 11th house
- Mars in Capricorn, 4th house

A planet's **zodiac sign** describes its position within the tropical zodiac, while its **house** represents its placement relative to the local horizon and meridian in the selected house system.

These calculations allow the application to present both planetary sign placements and house placements.

### 7. Generating Interpretations

After calculating planetary positions and houses, AstroCard retrieves the corresponding descriptions from its PostgreSQL database.

The user can select a planet through Telegram's interactive buttons and read the interpretation associated with its placement.

---

##  Calculation Engine

AstroCard uses the Swiss Ephemeris Java library for astronomical calculations.

This approach allows AstroCard to rely on established astronomical algorithms rather than implementing planetary orbital calculations from scratch.

---

##  Documentation

For more detailed information about the project,
see the following document:
- [Software Requirements](images/AstroCard_Software_requirements_specification.docx)
