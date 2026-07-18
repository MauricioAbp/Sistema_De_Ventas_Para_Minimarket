export interface LoginRequest {
  username: string;
  password: string;
}

export interface MfaRequest {
  usuarioId: number;
  code: string;
}

export interface LoginResponse {
  idUsuario: number;
  nombre: string;
  apellido: string;
  username: string;
  rol: 'ADMIN' | 'CAJERO';
  token?: string;
  mfaRequired?: boolean;
}
