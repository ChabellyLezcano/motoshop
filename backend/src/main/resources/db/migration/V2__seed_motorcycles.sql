-- ============================================================
-- V2: siembra del catalogo. 30 motocicletas representativas de
-- todas las categorias del mercado europeo 2024.
--
-- Los datos tecnicos provienen de fichas oficiales de cada fabricante
-- y son aproximaciones razonables. La precision absoluta no es
-- critica para este TFM (banco de pruebas de arquitectura).
-- Precios en centimos. Cilindrada en cc. Pesos en orden de marcha.
-- ============================================================

INSERT INTO motorcycles (
    brand, model, displacement, year, price_cents, stock, description,
    power_hp, torque_nm, top_speed_kmh, engine_type, cooling,
    weight_kg, seat_height_mm, fuel_capacity_l, color,
    category, license, transmission, abs, traction_control
) VALUES
-- Honda CB650R
('Honda', 'CB650R', 649, 2024, 890000, 5, 'Naked deportiva de cuatro cilindros en linea, suspension Showa SFF-BP y control de traccion HSTC.', 94, 63, 210, 'INLINE_FOUR', 'LIQUID', 206, 810, 15.4, 'Mat Gunpowder Black', 'NAKED', 'A', 6, TRUE, TRUE),

-- Yamaha MT-07
('Yamaha', 'MT-07', 689, 2024, 790000, 8, 'Bicilindrica CP2 de 270 grados, agil y manejable, referencia absoluta del segmento medio.', 74, 67, 205, 'PARALLEL_TWIN', 'LIQUID', 184, 805, 14.0, 'Cyan Storm', 'NAKED', 'A', 6, TRUE, FALSE),

-- Kawasaki Z900
('Kawasaki', 'Z900', 948, 2024, 1050000, 3, 'Cuatro cilindros en linea con electronica de ultima generacion, modos de conduccion y TFT a color.', 125, 98, 240, 'INLINE_FOUR', 'LIQUID', 212, 795, 17.0, 'Metallic Spark Black', 'NAKED', 'A', 6, TRUE, TRUE),

-- Suzuki GSX-S1000
('Suzuki', 'GSX-S1000', 999, 2024, 1290000, 2, 'Hyper-naked derivada del legendario K5, electronica avanzada con cinco modos de conduccion.', 152, 106, 250, 'INLINE_FOUR', 'LIQUID', 214, 810, 19.0, 'Metallic Triton Blue', 'NAKED', 'A', 6, TRUE, TRUE),

-- Ducati Monster 937
('Ducati', 'Monster 937', 937, 2024, 1399000, 1, 'Bastidor monocasco aluminio, motor Testastretta 11 grados, control electronico completo Ducati.', 111, 93, 230, 'V_TWIN', 'LIQUID', 188, 820, 14.0, 'Ducati Red', 'NAKED', 'A', 6, TRUE, TRUE),

-- KTM 390 Duke
('KTM', '390 Duke', 399, 2024, 599000, 6, 'Monocilindrica LC4c, perfecta para el carnet A2, pantalla TFT a color y chasis tubular naranja.', 44, 39, 165, 'SINGLE', 'LIQUID', 165, 820, 15.0, 'Electronic Orange', 'NAKED', 'A2', 6, TRUE, TRUE),

-- BMW R 1250 GS
('BMW', 'R 1250 GS', 1254, 2024, 1850000, 2, 'Trail emblematica, motor boxer con ShiftCam, modos Pro, conectividad TFT 6.5 pulgadas y Dynamic ESA.', 136, 143, 200, 'BOXER', 'LIQUID', 249, 850, 20.0, 'Triple Black', 'TRAIL', 'A', 6, TRUE, TRUE),

-- Triumph Street Triple 765
('Triumph', 'Street Triple 765', 765, 2024, 1099000, 4, 'Tres cilindros, IMU de seis ejes, suspensiones Showa Big Piston y frenos Brembo Stylema.', 123, 80, 230, 'INLINE_TRIPLE', 'LIQUID', 188, 826, 15.0, 'Carnival Red', 'NAKED', 'A', 6, TRUE, TRUE),

-- Harley-Davidson Iron 883
('Harley-Davidson', 'Iron 883', 883, 2024, 750000, 3, 'Cruiser iconica, motor Evolution V-Twin refrigerado por aire, estilo minimalista y asiento bajo.', 50, 70, 170, 'V_TWIN', 'AIR', 256, 760, 12.5, 'Vivid Black', 'CRUISER', 'A', 5, TRUE, FALSE),

-- Ducati Streetfighter V4
('Ducati', 'Streetfighter V4', 1103, 2024, 2200000, 1, 'Naked de altas prestaciones derivada de la Panigale, motor Desmosedici Stradale V4 contrarrotante.', 208, 123, 285, 'V_FOUR', 'LIQUID', 199, 845, 16.0, 'Ducati Red', 'NAKED', 'A', 6, TRUE, TRUE),

-- Honda Africa Twin CRF1100L
('Honda', 'Africa Twin CRF1100L', 1084, 2024, 1535000, 3, 'Maxi-trail bicilindrica con pantalla tactil TFT, Apple CarPlay y modos de conduccion off-road.', 102, 105, 220, 'PARALLEL_TWIN', 'LIQUID', 226, 850, 18.8, 'Pearl Glare White', 'ADVENTURE', 'A', 6, TRUE, TRUE),

-- Yamaha Ténéré 700
('Yamaha', 'Tenere 700', 689, 2024, 1099000, 4, 'Trail aventurera pura con motor CP2, chasis tubular, suspensiones KYB y rueda delantera de 21 pulgadas.', 73, 68, 200, 'PARALLEL_TWIN', 'LIQUID', 204, 875, 16.0, 'Ceramic Ice', 'ADVENTURE', 'A', 6, TRUE, FALSE),

-- Kawasaki Ninja 400
('Kawasaki', 'Ninja 400', 399, 2024, 699000, 5, 'Deportiva ligera para carnet A2, chasis multitubular inspirado en H2, KRT Edition.', 45, 38, 190, 'PARALLEL_TWIN', 'LIQUID', 168, 785, 14.0, 'Lime Green / Ebony', 'SPORT', 'A2', 6, TRUE, FALSE),

-- Suzuki V-Strom 800DE
('Suzuki', 'V-Strom 800DE', 776, 2024, 1179000, 3, 'Trail aventurera con motor bicilindrico en paralelo, rueda delantera de 21 pulgadas y modo Gravel.', 84, 78, 200, 'PARALLEL_TWIN', 'LIQUID', 230, 855, 20.0, 'Champion Yellow No.2', 'ADVENTURE', 'A', 6, TRUE, TRUE),

-- Ducati Panigale V4
('Ducati', 'Panigale V4', 1103, 2024, 3189000, 1, 'Superbike de carreras con motor Desmosedici Stradale derivado de MotoGP, IMU Bosch y winglets aerodinamicos.', 215, 123, 299, 'V_FOUR', 'LIQUID', 198, 850, 16.0, 'Ducati Red', 'SUPERSPORT', 'A', 6, TRUE, TRUE),

-- KTM 890 Adventure
('KTM', '890 Adventure', 889, 2024, 1459000, 2, 'Trail de alto rendimiento, motor LC8c bicilindrico, electronica off-road avanzada y modo Rally.', 105, 100, 215, 'PARALLEL_TWIN', 'LIQUID', 196, 830, 20.0, 'Black Orange', 'ADVENTURE', 'A', 6, TRUE, TRUE),

-- BMW F 900 R
('BMW', 'F 900 R', 895, 2024, 965000, 4, 'Dynamic Roadster bicilindrica con conectividad integrada, modos Rain y Road, y ergonomia activa.', 105, 92, 215, 'PARALLEL_TWIN', 'LIQUID', 211, 815, 13.0, 'Racing Red', 'NAKED', 'A', 6, TRUE, TRUE),

-- Triumph Tiger 900 GT Pro
('Triumph', 'Tiger 900 GT Pro', 888, 2024, 1630000, 2, 'Trail asfaltera tricilindrica con suspension trasera electronica Marzocchi y pantalla TFT 7 pulgadas.', 108, 90, 215, 'INLINE_TRIPLE', 'LIQUID', 219, 820, 20.0, 'Sapphire Black', 'TRAIL', 'A', 6, TRUE, TRUE),

-- Yamaha MT-09 SP
('Yamaha', 'MT-09 SP', 890, 2024, 1299000, 3, 'Tricilindrica CP3 con suspensiones Ohlins y KYB ajustables, frenos Brembo Stylema y quickshifter.', 119, 93, 230, 'INLINE_TRIPLE', 'LIQUID', 190, 825, 14.0, 'Icon Performance', 'NAKED', 'A', 6, TRUE, TRUE),

-- Honda CB500X
('Honda', 'CB500X', 471, 2024, 735000, 6, 'Crossover ideal para carnet A2, embrague antirrebote, luces full LED y pantalla LCD negativa.', 47, 43, 175, 'PARALLEL_TWIN', 'LIQUID', 199, 830, 17.7, 'Grand Prix Red', 'TRAIL', 'A2', 6, TRUE, FALSE),

-- Kawasaki Z650
('Kawasaki', 'Z650', 649, 2024, 745000, 5, 'Naked compacta, chasis ligero, instrumentacion TFT con conexion smartphone via Bluetooth.', 68, 64, 210, 'PARALLEL_TWIN', 'LIQUID', 187, 790, 15.0, 'Metallic Spark Black', 'NAKED', 'A2', 6, TRUE, FALSE),

-- Ducati Multistrada V4 S
('Ducati', 'Multistrada V4 S', 1158, 2024, 2599000, 2, 'Maxi-trail de gran turismo con motor V4 Granturismo y radar frontal con control de crucero adaptativo.', 170, 121, 250, 'V_FOUR', 'LIQUID', 240, 840, 22.0, 'Ducati Red', 'ADVENTURE', 'A', 6, TRUE, TRUE),

-- BMW S 1000 RR
('BMW', 'S 1000 RR', 999, 2024, 2350000, 1, 'Superdeportiva de cuatro cilindros con BMW ShiftCam, 210 CV, modos Pro y winglets aerodinamicos.', 210, 113, 299, 'INLINE_FOUR', 'LIQUID', 197, 824, 16.5, 'Light White / M Motorsport', 'SUPERSPORT', 'A', 6, TRUE, TRUE),

-- KTM 1290 Super Duke R
('KTM', '1290 Super Duke R', 1301, 2024, 2049000, 2, 'Hyper-naked extrema con motor LC8 en V a 75 grados, chasis superligero y electronica Track Pack.', 180, 140, 280, 'V_TWIN', 'LIQUID', 200, 835, 16.0, 'Electronic Orange', 'NAKED', 'A', 6, TRUE, TRUE),

-- Suzuki Hayabusa
('Suzuki', 'Hayabusa', 1340, 2024, 1959000, 1, 'Leyenda del sport-turismo, aerodinamica optimizada y motor tetracilindrico de alta cilindrada.', 190, 150, 299, 'INLINE_FOUR', 'LIQUID', 264, 800, 20.0, 'Glass Sparkle Black', 'SPORT', 'A', 6, TRUE, TRUE),

-- Triumph Bonneville T120
('Triumph', 'Bonneville T120', 1200, 2024, 1425000, 3, 'Icono clasico moderno, motor bicilindrico de alto par a bajas revoluciones, acabados cromados.', 80, 105, 200, 'PARALLEL_TWIN', 'LIQUID', 236, 790, 14.5, 'Jet Black', 'CLASSIC', 'A', 6, TRUE, TRUE),

-- Yamaha XMAX 300
('Yamaha', 'XMAX 300', 292, 2024, 649000, 8, 'Maxiscooter premium con control de traccion, hueco bajo el asiento para dos cascos integrales.', 28, 29, 145, 'SINGLE', 'LIQUID', 179, 795, 13.0, 'Sonic Grey', 'SCOOTER', 'A2', 1, TRUE, TRUE),

-- Honda X-ADV
('Honda', 'X-ADV', 745, 2024, 1275000, 4, 'Crossover que combina scooter y trail, transmision DCT de doble embrague y modos de conduccion.', 58, 69, 175, 'PARALLEL_TWIN', 'LIQUID', 236, 820, 13.2, 'Pearl Deep Mud Gray', 'SCOOTER', 'A2', 6, TRUE, TRUE),

-- Royal Enfield Interceptor 650
('Royal Enfield', 'Interceptor 650', 648, 2024, 699000, 5, 'Roadster de estilo retro clasico, motor bicilindrico refrigerado por aire y aceite, escapes cromados.', 47, 52, 170, 'PARALLEL_TWIN', 'AIR', 217, 804, 13.7, 'Canyon Red', 'CLASSIC', 'A2', 6, TRUE, FALSE),

-- Aprilia RS 660
('Aprilia', 'RS 660', 659, 2024, 1189000, 3, 'Deportiva bicilindrica de peso medio con paquete electronico APRC de serie, IMU y quickshifter.', 100, 67, 240, 'PARALLEL_TWIN', 'LIQUID', 183, 820, 15.0, 'Apex Black', 'SPORT', 'A', 6, TRUE, TRUE);