import createClient from "openapi-fetch";
import { paths } from "./schema";

export const client = createClient<paths>({
  baseUrl: "http://localhost:8080",
  headers: {
    "Content-Type": "application/json",
  },
});

export const clientFormData = createClient<paths>({
  baseUrl: "http://localhost:8080",
  headers: {
    "Content-Type": "multipart/form-data",
  },
});

