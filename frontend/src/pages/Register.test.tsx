import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { MemoryRouter, useNavigate } from 'react-router-dom';
import { vi, describe, it, expect, beforeEach } from 'vitest';
import Register from './Register';
import { useAuthStore } from '../store/useAuthStore';
import api from '../lib/axios';

vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return {
    ...actual,
    useNavigate: vi.fn(),
  };
});

vi.mock('../store/useAuthStore', () => ({
  useAuthStore: vi.fn(),
}));

vi.mock('../lib/axios', () => ({
  default: {
    post: vi.fn(),
  },
}));

describe('Register Component', () => {
  const mockNavigate = vi.fn();
  const mockSetAuth = vi.fn();

  beforeEach(() => {
    vi.clearAllMocks();
    (useNavigate as ReturnType<typeof vi.fn>).mockReturnValue(mockNavigate);
    (useAuthStore as unknown as ReturnType<typeof vi.fn>).mockImplementation((selector: any) =>
      selector({ setAuth: mockSetAuth })
    );
  });

  it('renders registration form correctly', () => {
    render(
      <MemoryRouter>
        <Register />
      </MemoryRouter>
    );

    expect(screen.getByText('Create an account')).toBeInTheDocument();
    expect(screen.getByPlaceholderText('Full Name')).toBeInTheDocument();
    expect(screen.getByPlaceholderText('Email address')).toBeInTheDocument();
    expect(screen.getByPlaceholderText('Password')).toBeInTheDocument();
    expect(screen.getByPlaceholderText('Address (Optional)')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Sign up' })).toBeInTheDocument();
  });

  it('shows error message on registration failure', async () => {
    (api.post as ReturnType<typeof vi.fn>).mockRejectedValueOnce({
      response: { data: { message: 'Email already exists' } },
    });

    render(
      <MemoryRouter>
        <Register />
      </MemoryRouter>
    );

    fireEvent.change(screen.getByPlaceholderText('Full Name'), { target: { value: 'John' } });
    fireEvent.change(screen.getByPlaceholderText('Email address'), { target: { value: 'test@test.com' } });
    fireEvent.change(screen.getByPlaceholderText('Password'), { target: { value: 'wrongpass' } });
    fireEvent.click(screen.getByRole('button', { name: 'Sign up' }));

    await waitFor(() => {
      expect(screen.getByText('Email already exists')).toBeInTheDocument();
    });
  });

  it('calls setAuth and navigates on successful registration', async () => {
    (api.post as ReturnType<typeof vi.fn>).mockResolvedValueOnce({
      data: { name: 'Test User', email: 'test@test.com', token: 'mockToken' },
    });

    render(
      <MemoryRouter>
        <Register />
      </MemoryRouter>
    );

    fireEvent.change(screen.getByPlaceholderText('Full Name'), { target: { value: 'Test User' } });
    fireEvent.change(screen.getByPlaceholderText('Email address'), { target: { value: 'test@test.com' } });
    fireEvent.change(screen.getByPlaceholderText('Password'), { target: { value: 'password123' } });
    fireEvent.click(screen.getByRole('button', { name: 'Sign up' }));

    await waitFor(() => {
      expect(mockSetAuth).toHaveBeenCalledWith(
        { name: 'Test User', email: 'test@test.com' },
        'mockToken'
      );
      expect(mockNavigate).toHaveBeenCalledWith('/dashboard');
    });
  });
});
