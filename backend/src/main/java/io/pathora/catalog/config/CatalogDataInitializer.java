package io.pathora.catalog.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CatalogDataInitializer implements ApplicationRunner {
  private final JdbcTemplate jdbc;

  public CatalogDataInitializer(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  @Override
  @Transactional
  public void run(ApplicationArguments args) {
    jdbc.update(
        """
        update schools set
          image_url = case name
            when 'Ingeniería' then 'https://images.unsplash.com/photo-1581092160607-ee22621dd758?auto=format&fit=crop&w=1400&q=85'
            when 'Negocios' then 'https://images.unsplash.com/photo-1556761175-b413da4baf72?auto=format&fit=crop&w=1400&q=85'
            when 'Artes' then 'https://images.unsplash.com/photo-1549490349-8643362247b5?auto=format&fit=crop&w=1400&q=85'
            when 'Ciencias Sociales' then 'https://images.unsplash.com/photo-1529156069898-49953e39b3ac?auto=format&fit=crop&w=1400&q=85'
            when 'Salud' then 'https://images.unsplash.com/photo-1576091160399-112ba8d25d1d?auto=format&fit=crop&w=1400&q=85'
            when 'Educación y Humanidades' then 'https://images.unsplash.com/photo-1503676260728-1c00da094a0b?auto=format&fit=crop&w=1400&q=85'
            else image_url
          end,
          description = case name
            when 'Ingeniería' then '## Diseñar, construir y transformar\n\nLa Escuela de Ingeniería conecta ciencias, modelado y experimentación para resolver desafíos de infraestructura, industria y tecnología. Los estudiantes aprenden mediante laboratorios, prototipos y proyectos con restricciones reales.\n\n### Una práctica responsable\n\nLa seguridad, la sostenibilidad y el impacto social acompañan cada decisión técnica, desde los fundamentos hasta el proyecto integrador.'
            when 'Negocios' then '## Ideas que se convierten en organizaciones\n\nLa Escuela de Negocios reúne estrategia, economía, datos y liderazgo. Sus programas preparan para comprender mercados, administrar recursos y crear valor de manera responsable.\n\n### Decisiones con perspectiva\n\nCasos, simulaciones y proyectos con organizaciones ayudan a desarrollar criterio financiero, capacidad de negociación y una visión ética del emprendimiento.'
            when 'Artes' then '## Crear para interpretar el mundo\n\nLa Escuela de Artes es un espacio de exploración visual, sonora y escénica. La formación combina historia, técnica y práctica de taller para que cada estudiante construya una voz propia.\n\n### Cultura en movimiento\n\nExposiciones, conciertos, montajes y proyectos colectivos conectan la creación con públicos, territorios y conversaciones contemporáneas.'
            when 'Ciencias Sociales' then '## Comprender personas, comunidades e instituciones\n\nLa Escuela de Ciencias Sociales estudia cómo vivimos juntos, cómo cambian las sociedades y cómo pueden diseñarse respuestas más justas. Integra teoría, métodos cualitativos, análisis de datos y trabajo territorial.\n\n### Escuchar antes de intervenir\n\nLa investigación y la colaboración con comunidades permiten convertir evidencia en políticas, programas y procesos de transformación social.'
            when 'Salud' then '## Ciencia al servicio del cuidado\n\nLa Escuela de Salud integra bases biomédicas, prevención y atención centrada en las personas. Sus programas combinan simulación, práctica supervisada e investigación basada en evidencia.\n\n### Cuidar con criterio y empatía\n\nEl aprendizaje promueve decisiones clínicas seguras, comunicación humana y trabajo interdisciplinario en hospitales, centros comunitarios y programas de bienestar.'
            else description
          end
        where name in ('Ingeniería','Negocios','Artes','Ciencias Sociales','Salud','Educación y Humanidades')
        """);

    jdbc.update(
        """
        insert into careers (code, name, image_url, description, school_id, duration_terms, study_mode, status, created_at, updated_at)
        select data.code, data.name, data.image, data.name, s.id, 12, data.mode, 'ACTIVE', now(), now()
        from (values
          ('ART-VIS','Artes Visuales','https://images.unsplash.com/photo-1541961017774-22349e4a1262?auto=format&fit=crop&w=1400&q=85','ON_CAMPUS','Artes'),
          ('ART-MUS','Música','https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?auto=format&fit=crop&w=1400&q=85','ON_CAMPUS','Artes'),
          ('ART-ESC','Artes Escénicas','https://images.unsplash.com/photo-1503095396549-807759245b35?auto=format&fit=crop&w=1400&q=85','ON_CAMPUS','Artes'),
          ('ART-GCU','Gestión Cultural','https://images.unsplash.com/photo-1488841714725-bb4c32d1ac94?auto=format&fit=crop&w=1400&q=85','HYBRID','Artes'),
          ('SOC-SOC','Sociología','https://images.unsplash.com/photo-1529156069898-49953e39b3ac?auto=format&fit=crop&w=1400&q=85','HYBRID','Ciencias Sociales'),
          ('SOC-TSO','Trabajo Social','https://images.unsplash.com/photo-1488521787991-ed7bbaae773c?auto=format&fit=crop&w=1400&q=85','ON_CAMPUS','Ciencias Sociales'),
          ('SOC-ANT','Antropología','https://images.unsplash.com/photo-1521295121783-8a321d551ad2?auto=format&fit=crop&w=1400&q=85','ON_CAMPUS','Ciencias Sociales'),
          ('SOC-DCM','Desarrollo Comunitario','https://images.unsplash.com/photo-1531206715517-5c0ba140b2b8?auto=format&fit=crop&w=1400&q=85','HYBRID','Ciencias Sociales')
        ) data(code,name,image,mode,school)
        join schools s on s.name = data.school
        on conflict (code) do update set name=excluded.name, image_url=excluded.image_url, school_id=excluded.school_id
        """);

    jdbc.update(
        """
        insert into pensums (career_id, version, description, effective_from, active, created_at)
        select c.id, '2026.1', 'Plan académico organizado en doce cuatrimestres con formación disciplinar, experiencias integradoras y asignaturas de formación general.', 2026, true, now()
        from careers c left join pensums p on p.career_id = c.id
        where p.id is null
        """);

    jdbc.update(
        """
        delete from subject_prerequisites prerequisite
        using pensum_subjects item, pensums p, careers c
        where item.pensum_id = p.id and p.career_id = c.id
          and item.term_number > c.duration_terms
          and (prerequisite.subject_id = item.subject_id or prerequisite.prerequisite_id = item.subject_id)
        """);
    jdbc.update(
        """
        delete from pensum_subjects item
        using pensums p, careers c
        where item.pensum_id = p.id and p.career_id = c.id
          and item.term_number > c.duration_terms
        """);

    jdbc.update(
        """
        with classified as (
          select c.id, c.name, s.name school,
            case
              when lower(c.name) ~ 'sistema|software|comput|datos|ciber|informática' then 'TECHNOLOGY'
              when lower(c.name) ~ 'ambient|ecolog|sosten' then 'ENVIRONMENT'
              when lower(c.name) ~ 'salud|medic|enfer|nutri|odont|terapia' then 'HEALTH'
              when lower(c.name) ~ 'administr|negocio|finanza|contab|mercadeo|econom' then 'BUSINESS'
              when lower(c.name) ~ 'arte|diseño|música|cine|comunicación' then 'CREATIVE'
              when lower(c.name) ~ 'derecho|social|psicolog|educa|política' then 'SOCIAL'
              when lower(s.name) ~ 'ingenier|tecnolog' then 'ENGINEERING'
              when lower(s.name) ~ 'salud' then 'HEALTH'
              when lower(s.name) ~ 'negocio' then 'BUSINESS'
              when lower(s.name) ~ 'arte' then 'CREATIVE'
              else 'GENERAL'
            end family
          from careers c join schools s on s.id = c.school_id
        ), content as (
          select *,
            case family
              when 'TECHNOLOGY' then name || ' combina programación, arquitectura tecnológica y análisis de datos para crear sistemas confiables que respondan a problemas reales.'
              when 'ENVIRONMENT' then name || ' estudia la relación entre territorio, recursos y sociedad para impulsar soluciones sostenibles con impacto medible.'
              when 'HEALTH' then name || ' integra ciencia, prevención y atención centrada en las personas para mejorar el bienestar individual y colectivo.'
              when 'BUSINESS' then name || ' conecta estrategia, análisis y liderazgo para transformar recursos e ideas en organizaciones responsables y sostenibles.'
              when 'CREATIVE' then name || ' convierte observación, técnica y sensibilidad cultural en propuestas capaces de comunicar, emocionar y transformar entornos.'
              when 'SOCIAL' then name || ' ofrece herramientas para comprender a las personas, las instituciones y los cambios sociales desde una mirada crítica y responsable.'
              when 'ENGINEERING' then name || ' articula ciencias, diseño y experimentación para resolver desafíos técnicos con seguridad, eficiencia y responsabilidad.'
              else name || ' desarrolla una mirada especializada mediante fundamentos, práctica aplicada e investigación conectada con su campo profesional.'
            end hero,
            case family
              when 'TECHNOLOGY' then 'La carrera avanza desde el pensamiento computacional y la programación hasta el diseño de plataformas, la gestión de datos y la seguridad. Los proyectos de cada etapa permiten integrar decisiones técnicas con necesidades humanas y organizacionales.'
              when 'ENVIRONMENT' then 'El programa combina ciencias ambientales, lectura del territorio, normativa y gestión de proyectos. El aprendizaje ocurre mediante análisis de casos, trabajo de campo y propuestas de intervención que equilibran desarrollo, conservación y participación comunitaria.'
              when 'HEALTH' then 'La formación recorre las bases biológicas y sociales de la salud, la evaluación profesional y la intervención basada en evidencia. Las prácticas guiadas fortalecen el juicio ético, la comunicación y el trabajo con equipos interdisciplinarios.'
              when 'BUSINESS' then 'El plan integra economía, procesos, datos y comportamiento organizacional. A través de simulaciones y proyectos, el estudiante aprende a diagnosticar escenarios, administrar recursos y tomar decisiones con visión estratégica.'
              when 'CREATIVE' then 'La experiencia formativa reúne historia, lenguaje visual, experimentación y producción. Talleres y proyectos ayudan a construir una voz propia, argumentar decisiones y responder a contextos culturales y tecnológicos cambiantes.'
              when 'SOCIAL' then 'El programa combina teoría social, investigación y análisis de contextos. Cada período amplía la capacidad de escuchar, interpretar evidencia y diseñar respuestas respetuosas de los derechos, la diversidad y la vida democrática.'
              when 'ENGINEERING' then 'La carrera conecta matemáticas y ciencias con modelado, diseño y validación. Los laboratorios y proyectos aumentan gradualmente en complejidad hasta abordar sistemas completos y restricciones del entorno profesional.'
              else 'La formación organiza fundamentos, métodos de investigación y experiencias aplicadas en una progresión clara. Cada período permite relacionar conceptos con casos y proyectos propios de ' || name || '.'
            end overview
          from classified
        )
        update careers c set
          hero_summary = content.hero,
          overview = content.overview,
          professional_profile = case content.family
            when 'TECHNOLOGY' then 'Podrás participar en equipos de desarrollo, arquitectura, datos, calidad o transformación digital, traduciendo necesidades complejas en productos tecnológicos mantenibles y seguros.'
            when 'ENVIRONMENT' then 'Podrás trabajar en gestión ambiental, evaluación de impacto, educación para la sostenibilidad, consultoría y proyectos territoriales junto a instituciones públicas, empresas y comunidades.'
            when 'HEALTH' then 'Podrás integrarte a servicios de salud, programas preventivos, investigación y proyectos comunitarios, actuando con rigor científico, empatía y responsabilidad profesional.'
            when 'BUSINESS' then 'Podrás asumir funciones de análisis, operaciones, consultoría, emprendimiento y dirección de proyectos en organizaciones públicas, privadas o del tercer sector.'
            when 'CREATIVE' then 'Podrás desarrollar proyectos de comunicación y producción creativa, colaborar con estudios, instituciones culturales y equipos interdisciplinarios, o construir una práctica independiente.'
            when 'SOCIAL' then 'Podrás aportar en investigación, educación, gestión pública, intervención social y organizaciones comunitarias mediante diagnósticos rigurosos y propuestas contextualizadas.'
            when 'ENGINEERING' then 'Podrás diseñar, evaluar y gestionar soluciones de ingeniería en empresas, consultoras, instituciones públicas y proyectos de innovación, considerando desempeño, riesgo e impacto.'
            else 'Podrás aplicar los métodos de ' || content.name || ' en organizaciones, proyectos de investigación e iniciativas de innovación, comunicando decisiones con claridad y criterio ético.'
          end,
          description = content.hero
        from content where content.id = c.id
        """);

    jdbc.update("delete from career_outcomes");
    jdbc.update(
        """
        insert into career_outcomes (career_id, position, outcome)
        select c.id, outcome.position, outcome.text
        from careers c join schools s on s.id = c.school_id
        cross join lateral (values
          (0, 'Interpretar problemas propios de ' || c.name || ' utilizando evidencia y fundamentos de la disciplina.'),
          (1, 'Diseñar y evaluar propuestas viables para contextos reales de ' || lower(s.name) || '.'),
          (2, 'Comunicar hallazgos y decisiones a públicos especializados y no especializados.'),
          (3, 'Liderar proyectos interdisciplinarios con responsabilidad ética, social y ambiental.')
        ) outcome(position, text)
        """);

    jdbc.update(
        """
        insert into subjects (code, name, description, credits, weekly_hours)
        select c.code || '-T' || lpad(term.number::text, 2, '0') || '-' || position.number,
               case position.number
                 when 1 then case mod(term.number, 4)
                   when 1 then 'Fundamentos de ' || c.name
                   when 2 then 'Métodos de Investigación en ' || c.name
                   when 3 then 'Análisis de Contextos en ' || c.name
                   else 'Teoría y Práctica de ' || c.name
                 end
                 else case mod(term.number, 4)
                   when 1 then 'Taller de Expresión y Proyecto'
                   when 2 then 'Herramientas para ' || c.name
                   when 3 then 'Laboratorio de Casos y Territorio'
                   else 'Proyecto Colaborativo de ' || c.name
                 end
               end || case when term.number <= 4 then ' I' when term.number <= 8 then ' II' else ' III' end,
               'Asignatura disciplinar de ' || c.name || ' que integra fundamentos, análisis y aplicación mediante ejercicios vinculados con su campo profesional.',
               case position.number when 1 then 3 else 4 end,
               case position.number when 1 then 4 else 6 end
        from pensums p join careers c on c.id = p.career_id
        cross join lateral generate_series(1, c.duration_terms) term(number)
        cross join generate_series(1, 2) position(number)
        where not exists (
          select 1 from pensum_subjects existing
          where existing.pensum_id = p.id
            and existing.term_number = term.number
            and existing.position = position.number
        )
        on conflict (code) do nothing
        """);
    jdbc.update(
        """
        insert into subjects (code, name, description, credits, weekly_hours)
        select c.code || '-T' || lpad(term.number::text, 2, '0') || '-' || position.number,
               case position.number
                 when 3 then 'Laboratorio Aplicado de ' || c.name || ' ' || term.number
                 else case mod(term.number, 6)
                   when 1 then 'Gestión Ambiental y Sostenibilidad'
                   when 2 then 'Comunicación y Escritura Académica'
                   when 3 then 'Ética y Responsabilidad Profesional'
                   when 4 then 'Ciudadanía y Pensamiento Social'
                   when 5 then 'Bienestar Integral y Salud'
                   else 'Innovación y Emprendimiento'
                 end
               end,
               case position.number
                 when 3 then 'Asignatura práctica orientada a laboratorios, casos y proyectos vinculados con el ejercicio profesional.'
                 else 'Espacio formativo para ciudadanía, sostenibilidad, comunicación, bienestar y desarrollo interdisciplinario.'
               end,
               case position.number when 3 then 3 else 2 end,
               case position.number when 3 then 4 else 3 end
        from pensums p
        join careers c on c.id = p.career_id
        cross join lateral generate_series(1, c.duration_terms) term(number)
        cross join generate_series(3, 4) position(number)
        on conflict (code) do nothing
        """);
    jdbc.update(
        """
        with generated as (
          select substring(s.code from '-T([0-9]{2})-[34]$')::int term,
                 right(s.code, 1)::int position, s.id subject_id,
                 c.id career_id, c.name career_name, sc.name school_name,
                 right(s.code, 1) = '4' and mod((c.id * 7 + substring(s.code from '-T([0-9]{2})-[34]$')::int * 3), 5) in (0, 1) formative
          from subjects s
          join careers c on s.code like c.code || '-T%-3' or s.code like c.code || '-T%-4'
          join schools sc on sc.id = c.school_id
        )
        update subjects s set
          name = case
            when g.formative then case mod((g.career_id + g.term)::int, 6)
              when 0 then 'Gestión Ambiental y Sostenibilidad'
              when 1 then 'Comunicación y Escritura Académica'
              when 2 then 'Ética y Responsabilidad Profesional'
              when 3 then 'Ciudadanía y Pensamiento Social'
              when 4 then 'Bienestar Integral y Salud'
              else 'Innovación y Emprendimiento'
            end
            when g.position = 3 then (case mod(g.term::int, 4)
              when 1 then 'Fundamentos Aplicados de ' || g.career_name
              when 2 then 'Métodos y Herramientas de ' || g.career_name
              when 3 then 'Taller de Casos en ' || g.career_name
              else 'Laboratorio de Soluciones en ' || g.career_name
            end) || case when g.term <= 4 then ' I' when g.term <= 8 then ' II' else ' III' end
            else (case mod(g.term::int, 3)
              when 0 then 'Proyecto Integrador de ' || g.career_name
              when 1 then 'Análisis Avanzado en ' || g.career_name
              else 'Seminario de Tendencias en ' || g.career_name
            end) || case when g.term <= 4 then ' I' when g.term <= 8 then ' II' else ' III' end
          end,
          description = case when g.formative
            then 'Asignatura de formación integral que conecta el desarrollo profesional con ciudadanía, bienestar, comunicación y sostenibilidad.'
            else 'Asignatura disciplinar de ' || g.career_name || ' orientada a resolver casos y proyectos con métodos propios del campo.'
          end
        from generated g where g.subject_id = s.id
        """);
    jdbc.update(
        """
        insert into pensum_subjects (pensum_id, subject_id, term_number, position, mandatory)
        select p.id, s.id, term.number, position.number, true
        from pensums p join careers c on c.id = p.career_id
        cross join lateral generate_series(1, c.duration_terms) term(number)
        cross join generate_series(1, 2) position(number)
        join subjects s on s.code = c.code || '-T' || lpad(term.number::text, 2, '0') || '-' || position.number
        where not exists (
          select 1 from pensum_subjects ps
          where ps.pensum_id = p.id and ps.term_number = term.number and ps.position = position.number
        )
        """);
    jdbc.update(
        """
        insert into pensum_subjects (pensum_id, subject_id, term_number, position, mandatory)
        select p.id, s.id, term.number, position.number, position.number = 3
        from pensums p
        join careers c on c.id = p.career_id
        cross join lateral generate_series(1, c.duration_terms) term(number)
        cross join generate_series(3, 4) position(number)
        join subjects s on s.code = c.code || '-T' || lpad(term.number::text, 2, '0') || '-' || position.number
        where not exists (
          select 1 from pensum_subjects ps
          where ps.pensum_id = p.id and ps.term_number = term.number and ps.position = position.number
        )
        """);
    jdbc.update(
        """
        delete from subject_prerequisites sp
        using subjects s
        where sp.subject_id = s.id and (s.code like '%-T%-3' or s.code like '%-T%-4')
        """);
    jdbc.update(
        """
        insert into subject_prerequisites (subject_id, prerequisite_id)
        select current_subject.subject_id, previous_subject.subject_id
        from pensum_subjects current_subject
        join subjects current_details on current_details.id = current_subject.subject_id
        join pensums p on p.id = current_subject.pensum_id
        join pensum_subjects previous_subject
          on previous_subject.pensum_id = current_subject.pensum_id
         and previous_subject.term_number = current_subject.term_number - 1
         and previous_subject.position = current_subject.position
        where current_subject.term_number > 1
          and (current_details.code like '%-T%-3' or current_details.code like '%-T%-4')
          and current_details.description like 'Asignatura disciplinar%'
          and mod((current_subject.term_number + current_subject.position + p.career_id)::int, 3) <> 0
        on conflict do nothing
        """);
    jdbc.update(
        """
        insert into subject_prerequisites (subject_id, prerequisite_id)
        select current_subject.subject_id, previous_subject.subject_id
        from pensum_subjects current_subject
        join subjects current_details on current_details.id = current_subject.subject_id
        join pensums p on p.id = current_subject.pensum_id
        join pensum_subjects previous_subject
          on previous_subject.pensum_id = current_subject.pensum_id
         and previous_subject.term_number = current_subject.term_number - 1
         and previous_subject.position = current_subject.position
        where current_subject.term_number > 1
          and (current_details.code like '%-T%-1' or current_details.code like '%-T%-2')
          and mod((current_subject.term_number + current_subject.position + p.career_id)::int, 4) <> 0
        on conflict do nothing
        """);
  }
}
