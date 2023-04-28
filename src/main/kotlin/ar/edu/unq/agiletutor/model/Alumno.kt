package ar.edu.unq.agiletutor.model

import jakarta.persistence.*
import org.jetbrains.annotations.NotNull
import java.io.Serializable

@Entity
@Table(name = "alumnos")
class Alumno : Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_alumno")
    var id: Long? = 0

    @Column(nullable = false)
    @NotNull("El nonbre es obligatorio")
    // @Size(min = 1, max = 30, message = "el campo name debe contener un minimo de 3 y un máximo de 30 caracteres")
    var name: String? = null

    @Column(nullable = false)
    @NotNull("el apellido es obligatorio")
    //@Size(min = 1, max = 30, message = "el campo surname debe contener un minimo de 3 y un máximo de 30 caracteres")
    var surname: String? = null

    @Column(nullable = false)
    @NotNull("El número identificador es obligatorio")
    //@Size(min = 1, max = 30)
    var identifier: String? = null

    @Column(nullable = false, unique = true)
    @NotNull("El mail es obligatorio")
    // @Email(message = "El formato de mail no es válido")
    // @Pattern(regexp = "^[^@]+@[^@]+\\.[a-zA-Z] {2,}$", message = "El formato de mail no es válido")
    var email: String? = null

    @OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.LAZY, mappedBy = "alumno")
    //@Size(min = 1, max = 1)
    var attendances: MutableSet<Asistencia> = HashSet()

    @Column(nullable = false)
    var attendancepercentage: Double? = null

    @Column
    var observations: String = ""

    constructor() : super() {}
    constructor(
        name: String?,
        surname: String?,
        identifier: String?,
        email: String?,
        attendances: MutableSet<Asistencia>,
        attendancepercentage: Double,
        observations: String
    ) : super() {
        this.name = name
        this.surname = surname
        this.identifier = identifier
        this.email = email
        this.attendances = attendances
        this.attendancepercentage = attendancepercentage
        this.observations = observations
        this.createAttendaceList()
    }

    fun createAttendaceList() {
        val attendances: MutableSet<Asistencia> = mutableSetOf();
        for (i in 1..6) {
            var attendace: Asistencia = Asistencia(this, i)
            attendances.add(attendace)
        }
        this.attendances = attendances
    }
}