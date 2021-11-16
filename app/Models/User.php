<?php

namespace App\Models;

use Illuminate\Contracts\Auth\MustVerifyEmail;
use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Foundation\Auth\User as Authenticatable;
use Illuminate\Notifications\Notifiable;
use Laravel\Sanctum\HasApiTokens;

class User extends Authenticatable
{
    use HasApiTokens, HasFactory, Notifiable;

    /**
     * The attributes that aren't mass assignable.
     *
     * @var array
     */
    protected $guarded = ['id'];

    /**
     * The attributes that should be cast.
     *
     * @var array
     */
    protected $casts = [
        // 'email_verified_at' => 'datetime',
        'birthday' => 'date',
    ];
    
    public function caretaker(){
        return $this->hasOne(PatientAssignment::class, 'caretaker_id', 'id');
    }

    public function patient(){
        return $this->hasMany(PatientAssignment::class, 'patient_id', 'id');
    }

    public function patientMedicine() {
        return $this->hasMany(PatientMedicine::class, 'patient_id');
    }
}
